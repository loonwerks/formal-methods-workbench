(*---------------------------------------------------------------------------*)
(* Misc. support functions.                                                  *)
(*---------------------------------------------------------------------------*)

structure MiscLib :> MiscLib =
struct

type substring = Substring.substring;

open HolKernel Feedback Abbrev boolTheory boolLib bossLib;

val ERR = mk_HOL_ERR "MiscLib";

fun unimplemented s = ERR s "not yet implemented";

fun optlist NONE = []
  | optlist (SOME x) = [x];

fun listopt [] = NONE
  | listopt [x] = SOME x
  | listopt otherwise = raise ERR "listopt" "list length >1";

fun dropPrefix (_::t1) (_::t2) = dropPrefix t1 t2
  | dropPrefix [] list = list
  | dropPrefix list [] = raise ERR "dropPrefix" "prefix too long";

val bigU = Lib.U;

fun no_dups [] = true
  | no_dups (h::t) = not (mem h t) andalso no_dups t;

(*---------------------------------------------------------------------------*)
(* Make a comb (M N), instantiating type of M in order to make its domain    *)
(* equal to type of N.                                                       *)
(*---------------------------------------------------------------------------*)
 
fun mk_icomb (M,N) =
 let val theta = match_type (fst(dom_rng(type_of M))) (type_of N)
 in mk_comb(inst theta M,N)
 end
 handle HOL_ERR _ => raise ERR "mk_icomb" "";

fun takel [] l = []
  | takel _ [] = raise ERR "takel" "second arg not long enough"
  | takel (h1::t1) (_::t2) = h1::takel t1 t2;

(*---------------------------------------------------------------------------*)
(* Make a comb, instantiating type of M in order to make its domain equal to *)
(* types of terms in tmlist.                                                 *)
(*---------------------------------------------------------------------------*)

fun list_mk_icomb (M,[]) = M
  | list_mk_icomb (M,tmlist) =
    let val (argtys,cty) = strip_fun(type_of M)
        val argtyl = takel argtys tmlist
        val pat = list_mk_fun(front_last(argtyl))
        val ob = list_mk_fun(front_last(map type_of tmlist))
        val theta = match_type pat ob
 in list_mk_comb(inst theta M,tmlist)
 end
 handle HOL_ERR _ => raise ERR "list_mk_icomb" "";

(*===========================================================================*)
(* Calculating dependencies among a unordered collection of definitions.     *)
(*===========================================================================*)

(*---------------------------------------------------------------------------*)
(* Transitive closure of relation rel given as list of pairs having form     *)
(*                                                                           *)
(*   (d,[r1,...,rn]).                                                        *)
(*                                                                           *)
(* The TC function operates on a list of elements of form                    *)
(*                                                                           *)
(*   (x,(Y,fringe))                                                          *)
(*                                                                           *)
(* where Y is the set of already seen fringe elements, and fringe is those   *)
(* elements of Y that we just "arrived at" (thinking of the relation as a    *)
(* directed graph). Steps in the graph are made from the fringe, in a        *)
(* breadth-first manner.                                                     *)
(*---------------------------------------------------------------------------*)

fun TC rels_0 =
 let fun step a = assoc a rels_0 handle HOL_ERR _ => []
     fun relstep rels (x,(Y,fringe)) =
       let val fringe' = bigU (map step fringe)
           val Y' = union Y fringe'
           val fringe'' = set_diff fringe' Y
       in (x,(Y',fringe''))
       end
     fun fringe_of (x,(Y,fringe)) = fringe
     fun steps rels =
      let val (nullrels,nnullrels) = List.partition (null o fringe_of) rels
      in if nnullrels = [] 
           then map (fn (x,(Y,_)) => (x,Y)) rels
           else steps (map (relstep rels) nnullrels @ nullrels)
      end
 in
   steps (map (fn (x,Y) => (x,(Y,Y))) rels_0)
 end;

(*---------------------------------------------------------------------------*)
(* Transitive closure of a list of pairs.                                    *)
(*---------------------------------------------------------------------------*)

fun trancl rel =
 let val field = itlist (fn (x,y) => fn acc => insert x (insert y acc)) rel []
     fun init x =
       let val range = 
              rev_itlist (fn (a,b) => fn acc => 
                      if a=x then insert b acc else acc) rel []
       in (x,range)
       end
 in
   TC (map init field)
 end;

(*---------------------------------------------------------------------------*)
(* Given a transitively closed relation, topsort it into dependency order,   *)
(* then build the mutually dependent chunks (cliques).                       *)
(*---------------------------------------------------------------------------*)

fun chunk [] acc = acc
  | chunk ((a,adeps)::t) acc =
      let val (bideps,rst) = List.partition (fn (b,bdeps) => mem a bdeps) t
      in chunk rst (((a,adeps)::bideps)::acc)
      end

fun cliques_of tcrel = 
 let fun depends_on (a,adeps) (b,bdeps) = mem b adeps andalso not (mem a bdeps)
 in
   chunk (Lib.topsort depends_on tcrel) []
 end;


(*---------------------------------------------------------------------------
 Examples.
val ex1 =
 [("a","b"), ("b","c"), ("b","d"), ("d","c"),
  ("a","e"), ("e","f"), ("f","e"), ("d","a"),
  ("f","g"), ("g","g"), ("g","i"), ("g","h"),
  ("i","h"), ("i","k"), ("h","j")];

val ex2 =  ("a","z")::ex1;
val ex3 =  ("z","a")::ex1;
val ex4 =  ("z","c")::ex3;
val ex5 =  ("c","z")::ex3;
val ex6 =  ("c","i")::ex3;

cliques_of (trancl ex1);
cliques_of (trancl ex2);
cliques_of (trancl ex3);
cliques_of (trancl ex4);
cliques_of (trancl ex5);
cliques_of (trancl ex6);
  --------------------------------------------------------------------------*)


(* possibly useful ...
val tmToString = 
 let val (minprint_ty,minprint_tm) = Parse.print_from_grammars Parse.min_grammars
     val minpp = Parse.pp_term_without_overloads_on ["INSERT","UNION","EMPTY","IN"] 
 in fn PP.pp_to_string 72 pp
 end;
*)

fun simpfrag_to_ssfrag {rewrs,convs} =
 let open simpLib
 in SSFRAG {name = NONE,
            convs = convs, rewrs = rewrs, 
            filter = NONE, dprocs = [], ac = [], congs = []}
 end;

(*---------------------------------------------------------------------------*)
(* Drop COND_BOOL_CLAUSES from rewrites used, since keeping them means that  *)
(* congruences for disjunction and conjunction have to be used, and that's   *)
(* yucky.                                                                    *)
(*---------------------------------------------------------------------------*)

val basic_ss = 
 let open simpLib boolSimps
     val literal_cong = prove(
    ``(v:'a = v') ==> (literal_case (f:'a -> 'b) v = literal_case f (I v'))``,
      DISCH_THEN SUBST_ALL_TAC THEN 
      REWRITE_TAC [literal_case_THM, combinTheory.I_THM])

  val literal_I_thm = prove(
  ``literal_case (f : 'a -> 'b) (I x) = f x``,
  REWRITE_TAC [combinTheory.I_THM, literal_case_THM])

  (* pinched from src/simp/src/boolSimps.sml *)
  val BOOL_ss = SSFRAG
    {name = SOME"BOOL",
     convs=[{name="BETA_CONV (beta reduction)",
             trace=2,
             key=SOME ([],``(\x:'a. y:'b) z``),
  	   conv=K (K BETA_CONV)}],
     rewrs=[REFL_CLAUSE,  EQ_CLAUSES,
            NOT_CLAUSES,  AND_CLAUSES,
            OR_CLAUSES,   IMP_CLAUSES,
            COND_CLAUSES, FORALL_SIMP,
            EXISTS_SIMP,  COND_ID,
            EXISTS_REFL, GSYM EXISTS_REFL,
            EXISTS_UNIQUE_REFL, GSYM EXISTS_UNIQUE_REFL,
            literal_I_thm,
            EXCLUDED_MIDDLE,
            ONCE_REWRITE_RULE [DISJ_COMM] EXCLUDED_MIDDLE,
            bool_case_thm,
            NOT_AND, 
            SELECT_REFL, SELECT_REFL_2, RES_FORALL_TRUE, RES_EXISTS_FALSE],
     congs = [literal_cong], filter = NONE, ac = [], dprocs = []}
   val bool_ss = pure_ss ++ BOOL_ss ++ NOT_ss ++ CONG_ss ++ UNWIND_ss
 in
   bool_ss ++ pairSimps.PAIR_ss ++ optionSimps.OPTION_ss ++
   sumSimps.SUM_ss ++ combinSimps.COMBIN_ss
 end;


val kstd_ss = 
 let open simpLib boolSimps
     val literal_cong = prove(
    ``(v:'a = v') ==> (literal_case (f:'a -> 'b) v = literal_case f (I v'))``,
      DISCH_THEN SUBST_ALL_TAC THEN 
      REWRITE_TAC [literal_case_THM, combinTheory.I_THM])

  val literal_I_thm = prove(
  ``literal_case (f : 'a -> 'b) (I x) = f x``,
  REWRITE_TAC [combinTheory.I_THM, literal_case_THM])

  (* pinched from src/simp/src/boolSimps.sml *)
  val BOOL_ss = SSFRAG
    {name = SOME"BOOL",
     convs=[{name="BETA_CONV (beta reduction)",
             trace=2,
             key=SOME ([],``(\x:'a. y:'b) z``),
  	   conv=K (K BETA_CONV)}],
     rewrs=[REFL_CLAUSE,  EQ_CLAUSES,
            NOT_CLAUSES,  AND_CLAUSES,
            OR_CLAUSES,   IMP_CLAUSES,
            COND_CLAUSES, FORALL_SIMP,
            EXISTS_SIMP,  COND_ID,
            EXISTS_REFL, GSYM EXISTS_REFL,
            EXISTS_UNIQUE_REFL, GSYM EXISTS_UNIQUE_REFL,
            literal_I_thm,
            EXCLUDED_MIDDLE,
            ONCE_REWRITE_RULE [DISJ_COMM] EXCLUDED_MIDDLE,
            bool_case_thm,
            NOT_AND,
            SELECT_REFL, SELECT_REFL_2, RES_FORALL_TRUE, RES_EXISTS_FALSE],
     congs = [literal_cong], filter = NONE, ac = [], dprocs = []}
   val bool_ss = pure_ss ++ BOOL_ss ++ NOT_ss ++ CONG_ss ++ UNWIND_ss
 in
   bool_ss ++ pairSimps.PAIR_ss ++ optionSimps.OPTION_ss ++
   numSimps.REDUCE_ss ++ sumSimps.SUM_ss ++ combinSimps.COMBIN_ss ++
   numSimps.ARITH_RWTS_ss
 end;

(*---------------------------------------------------------------------------*)
(* y has x as a subterm                                                      *)
(*---------------------------------------------------------------------------*)

fun subterm x y = Lib.can (find_term (aconv x)) y;

val empty_tyset = HOLset.empty Type.compare

fun consts_of tm = 
 let fun consts [] acc = acc
       | consts (h::t) acc = 
         if is_comb h
          then let val (t1,t2) = dest_comb h
               in consts (t1::t2::t) acc
               end else
         if is_abs h then consts (body h::t) acc else
         if is_var h then consts t acc
         else consts t (HOLset.add(acc,h))
 in 
    consts [tm] Term.empty_tmset
 end

fun zip3 [] [] [] = []
  | zip3 (h1::t1) (h2::t2) (h3::t3) = (h1,h2,h3) :: zip3 t1 t2 t3
  | zip3 any other thing = raise ERR "zip3" "differing lengths";


(*---------------------------------------------------------------------------*)
(* Maps between qids in ML and in HOL.                                       *)
(*---------------------------------------------------------------------------*)

fun qid_to_tm (s1,s2) = 
 let open stringSyntax pairSyntax
 in mk_pair(fromMLstring s1,fromMLstring s2)
 end;

fun tm_to_qid tm =
 let open pairSyntax stringSyntax
 in (fromHOLstring##fromHOLstring)(dest_pair tm)
 end;

val dom = fst o dom_rng;
val rng = snd o dom_rng;


(*---------------------------------------------------------------------------*)
(* Map a record update term to a record projection term, and vice-versa.     *)
(* Kind of gross.                                                            *)
(*---------------------------------------------------------------------------*)

fun fupd_to_proj fupd =  (* can be simpler *)
 let val {Name,Thy,Ty} = dest_thy_const fupd
     val fnrecdty = snd(strip_fun Ty)
     val fnrecd_name = fst(dest_type fnrecdty)
     val i = String.size (fnrecd_name^"_")
     val j = String.size Name - String.size"_fupd"
     val fldname = String.extract(Name, i, SOME (j-i))
 in 
    prim_mk_const{Name=fnrecd_name^"_"^fldname,Thy=Thy} 
 end

fun proj_to_fupd proj = (* can be simpler *)
 let val {Name,Thy,Ty} = dest_thy_const proj
     val fnrecd_name = fst(dest_type (dom Ty))
     val i = String.size (fnrecd_name^"_")
     val fldname = String.extract(Name, i, NONE)
 in 
    prim_mk_const{Name=fnrecd_name^"_"^fldname^"_fupd",Thy=Thy} 
 end

(*---------------------------------------------------------------------------*)
(* Projecting a program variable out of the state record.                    *)
(*---------------------------------------------------------------------------*)

fun mk_gproj state = 
 let val {Tyop=styName,Thy,...} = dest_thy_type (type_of state)
 in prim_mk_const{Name = styName^"_global", Thy=Thy}
    handle HOL_ERR _ => mk_arb ind
 end

fun mk_fnproj state fname = 
 let val {Tyop=styName,Thy,...} = dest_thy_type (type_of state)
 in prim_mk_const{Name = styName^"_"^fname, Thy=Thy}
    handle HOL_ERR _ => mk_gproj state
 end

(*---------------------------------------------------------------------------*)
(* FAPPLY_FUPDATE_CONV eqc ``FAPPLY (FEMPTY |+ (k1,x1) |+ ...|+ (kn,xn)) k`` *)
(* returns                                                                   *)
(*                                                                           *)
(*   |- FAPPLY (FEMPTY |+ (k1,x1) |+ ... |+ (kn,xn)) k = x                   *)
(*                                                                           *)
(* provided that (ki,x) is the first binding in the fmap such that eqc       *)
(* proves |- ki = k.                                                         *)
(*                                                                           *)
(* Should go to finite_mapLib. Note that TEST_CONV attacks b in term         *)
(*                                                                           *)
(*   M = if b then x else y                                                  *)
(*---------------------------------------------------------------------------*)

fun TEST_CONV eqthm = 
  (RAND_CONV o RATOR_CONV o RATOR_CONV o RAND_CONV) (REWR_CONV eqthm)
  THENC
  RAND_CONV COND_CONV;

fun FAPPLY_FUPDATE_CONV key_eq_conv tm = 
 let open finite_mapSyntax finite_mapTheory
     val (fmap,key) = dest_fapply tm
     fun steps fmap = 
      if is_fempty fmap 
        then raise ERR "FAPPLY_MAP_CONV" "given key not in map domain"
        else let val (rst,bind) = dest_fupdate fmap
                 val (ki,x) = pairSyntax.dest_pair bind
                 val eqthm = key_eq_conv (mk_eq(key,ki))
                 val fupd_thm = ISPECL [rst,ki,x,key] FAPPLY_FUPDATE_THM
                 val fupd_thm' = CONV_RULE (TEST_CONV eqthm) fupd_thm
             in if snd(dest_eq(concl eqthm)) = boolSyntax.T
                 then fupd_thm'
                 else let val subthm = steps rst
                      in TRANS fupd_thm' subthm
                      end
             end
 in steps fmap
 end;

(*---------------------------------------------------------------------------*)
(* Eliminate simple lets.                                                    *)
(*---------------------------------------------------------------------------*)

fun TRIV_LET_CONV tm = 
 let val (_,a) = boolSyntax.dest_let tm
 in if is_var a orelse is_const a orelse 
    Literal.is_literal a
    then (REWR_CONV boolTheory.LET_THM THENC BETA_CONV)
    else NO_CONV
 end tm;

fun front_last2 [x,y] = ([],(x,y))
  | front_last2 (h::h1::t) = 
     let val (front,last2) = front_last2 (h1::t)
     in (h::front,last2)
     end
  | front_last2 _ = raise ERR "front_last2" "list length shorter than 2";

(*---------------------------------------------------------------------------*)
(* Attempt to control HOL output                                             *)
(*---------------------------------------------------------------------------*)

fun silent true =
     let open Feedback
     in
       emit_MESG := false;   (* emit_ERR still true *)
       emit_WARNING := false;
       set_trace "Define.storage_message" 0;
       set_trace "Theory.save_thm_reporting" 0;
       set_trace "Vartype Format Complaint" 0;
       set_trace "meson" 0;
       set_trace "metis" 0;
       set_trace "notify type variable guesses" 0
     end
  | silent false = 
     let open Feedback
     in
       emit_MESG := true;
       emit_WARNING := true;
       set_trace "Define.storage_message" 1;
       set_trace "Theory.save_thm_reporting" 1;
       set_trace "Vartype Format Complaint" 1;
       set_trace "meson" 1;
       set_trace "metis" 1;
       set_trace "notify type variable guesses" 1
     end

val _ = silent true;

fun list x = [x];

(*---------------------------------------------------------------------------*)
(* Variants                                                                  *)
(*---------------------------------------------------------------------------*)

fun gen_variant f slist s = 
 let fun vary s = if Lib.mem s slist then vary (f s) else s
 in vary s
 end;

fun prefix_string_variant prefix =
 let fun delta s = String.concat [prefix,s]
 in gen_variant delta
 end

fun suffix_string_variant suffix =
 let fun delta s = String.concat [s,suffix]
 in gen_variant delta
 end

fun numeric_string_variant spacer  =
 let val counter = ref 0
     fun delta s = 
      let val s' = String.concat [s,spacer,Int.toString (!counter)]
      in counter := !counter + 1;
         s'
      end
 in gen_variant delta
 end

(*---------------------------------------------------------------------------*)
(* Variants of a list of HOL variables.                                      *)
(*---------------------------------------------------------------------------*)

fun vary v (olist,vlist) =
   let val v' = variant vlist v
   in (v'::olist, v'::vlist)
   end;

(*---------------------------------------------------------------------------*)
(* Rename elements of tlist away from vlist.                                 *)
(*---------------------------------------------------------------------------*)

fun variants vlist tlist = 
 let val (olist,vlist') = rev_itlist vary tlist ([],vlist)
 in rev olist
 end;

fun mk_gensym prefix base0 = 
 let val idStrm = 
       let fun idName n = prefix^(if n <= 0 then "" else Int.toString n)
       in Lib.mk_istream (fn x => x + 1) 0 idName
       end
     val base = ref base0 : string list ref
     fun set_base slist = (!base before (base := slist))
     fun gensym () = 
       let val id = Lib.state idStrm
       in if mem id (!base)
          then (Lib.next idStrm; gensym())
          else (base := id :: !base; id)
       end
 in {gensym = gensym, set_base = set_base}
 end

fun is_atom tm = is_const tm orelse is_var tm;

fun succeed() = OS.Process.terminate OS.Process.success
fun fail()    = OS.Process.terminate OS.Process.failure;

fun stdOut_print s = let open TextIO in output(stdOut,s); flushOut stdOut end;
fun stdErr_print s = let open TextIO in output(stdErr,s); flushOut stdErr end;

fun inputFile s = 
 let open TextIO
     val istrm = openIn s handle _
     => raise ERR "inputFile" 
            ("unable to open file "^s^" from directory "^FileSys.getDir())
 in inputAll istrm before closeIn istrm
 end;

fun stringToFile s filename =
 let open TextIO
     val ostrm = openOut filename
 in output(ostrm, s) handle e => (closeOut ostrm; raise e);
    closeOut ostrm
 end;

fun prettyStream ostrm (succmesg,failmesg) pretty = 
 let open TextIO PolyML
     fun out s = output(ostrm,s)
     val _ = prettyPrint (out,75) pretty
     val _ = stdOut_print succmesg
 in 
    ()
 end handle _ => (stdErr_print failmesg; TextIO.closeOut ostrm);


fun prettyFile filename pretty = 
 let open TextIO PolyML
     val ostrm = openOut filename
 in prettyStream ostrm 
          ("  Wrote file: "^filename^"\n",
           "Failure on output to opened file: "^filename)
       pretty
   ; flushOut ostrm
   ; closeOut ostrm handle _ => ()
 end

fun apply_with_chatter f x prefix postfix = 
 let val _ = stdErr_print prefix 
     val result = f x handle e => 
                  (stdErr_print "failed.\n";
                   stdErr_print (Feedback.exn_to_string e); fail())
     val _ = stdErr_print postfix
 in 
   result
 end

fun exp x n = if n <= 0 then 1 else x * exp x (n-1);

fun is_pow2 n = 
 if n <= 0 then false else
 if n = 1 then true 
  else (n mod 2 = 0) andalso is_pow2 (n div 2) ;

(*---------------------------------------------------------------------------*)
(* Find the nearest power of two >= n                                        *)
(*---------------------------------------------------------------------------*)

fun bits_needed n = 
 let fun aux len = 
      let val bound = exp 2 len
      in if n < bound andalso is_pow2 len then len
         else aux (len + 1)
      end
 in aux 0
 end;

fun inv_image R f x y = R (f x) (f y);

fun sort_on_int_key list = Lib.sort (inv_image (curry op Int.<=) fst) list;
fun sort_on_string_key list = Lib.sort (inv_image (curry op String.<=) fst) list;

fun sort_on_qid_key list = 
 let fun leq (s1,s2) (t1,t2) = String.<= (s1^s2,t1^t2)
 in Lib.sort (inv_image leq fst) list
 end;


(*---------------------------------------------------------------------------
 Following are unused, but may be useful again.
fun classes P [] = []
  | classes P (h::t)= 
     let val (pass,fail) = Lib.partition (P h) t
     in (h::pass)::classes P fail
     end;

(*---------------------------------------------------------------------------*)
(* Basic conversion                                                          *)
(*---------------------------------------------------------------------------*)

val EQT_THM = METIS_PROVE [] ``!x. x = (x = T)``;

fun EQT_INTRO_CONV tm = 
 (if is_eq tm then NO_CONV
  else if type_of tm = bool
       then REWR_CONV (SPEC_ALL EQT_THM)
       else NO_CONV) tm;

*)

fun mapfilter f list =
  let fun mapf [] acc = rev acc
        | mapf (h::t) acc = 
             case total f h
               of NONE => mapf t acc
               |  SOME h' => mapf t (h'::acc)
  in 
   mapf list []
  end;

end (* MiscLib *)
