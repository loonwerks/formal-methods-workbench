(*===========================================================================*)
(* Load and open required context                                            *)
(*===========================================================================*)

open HolKernel Parse boolLib bossLib BasicProvers;

open arithmeticTheory listTheory stringTheory 
     charsetTheory FormalLangTheory regexpTheory regexpLib 
     ASCIInumbersTheory numposrepTheory ASCIInumbersLib;

(*---------------------------------------------------------------------------*)
(* Boilerplate prelude stuff                                                 *)
(*---------------------------------------------------------------------------*)

val _ = numLib.prefer_num();

infix byA;
val op byA = BasicProvers.byA;

val qpat_k_assum = Lib.C qpat_x_assum kall_tac;

fun qspec q th = th |> Q.SPEC q 
fun qspec_arith q th = qspec q th |> SIMP_RULE arith_ss [];

val var_eq_tac = rpt BasicProvers.VAR_EQ_TAC;

val decide = bossLib.DECIDE;
val qdecide = decide o Parse.Term;

(*---------------------------------------------------------------------------*)
(* Let's get started                                                         *)
(*---------------------------------------------------------------------------*)

val _ = new_theory "splat";

(*---------------------------------------------------------------------------*)
(* Definitions                                                               *)
(*---------------------------------------------------------------------------*)

val layout_def =  (* LSB with padding to width *)
 Define
  `layout b n width = PAD_RIGHT 0n width (n2l b n)`;

val enc_def = Define `enc w n = MAP CHR (layout 256 n w)`;
val dec_def = Define `dec s = l2n 256 (MAP ORD s)`;

(*---------------------------------------------------------------------------*)
(* Width needed to encode number n in base 256                               *)
(*---------------------------------------------------------------------------*)

val width_def = 
 Define 
  `width n = if n = 0 then 1 else SUC (LOG 256 n)`;

val n2l_256 = save_thm
("n2l_256",
 n2l_def 
  |> Q.SPECL [`n`,`256`] 
  |> SIMP_RULE arith_ss []
  |> Q.GEN `n`
);

val ord_mod_256 = Q.store_thm
("ord_mod_256",
 `!c. ORD c MOD 256 = ORD c`,
 rw_tac arith_ss [ORD_BOUND]);

val MAP_ORD_CHR = Q.prove
(`!list. EVERY ($> 256) list ==> (MAP (ORD o CHR) list = list)`,
 Induct >> rw_tac list_ss [ORD_CHR_RWT]);

val l2n_append_zeros = Q.prove
(`!n list. l2n 256 (list ++ GENLIST (K 0) n) = l2n 256 list`,
Induct 
 >> rw_tac list_ss [GENLIST]
 >> metis_tac [APPEND_SNOC, qspec_arith `256` l2n_SNOC_0]);

(*---------------------------------------------------------------------------*)
(* Invertibility for primitive encoder/decoder                               *)
(*---------------------------------------------------------------------------*)

val dec_enc = Q.store_thm
("dec_enc",
 `!n w. dec (enc w n) = n`,
 rw_tac list_ss [enc_def, dec_def,layout_def,MAP_MAP_o,
    PAD_RIGHT,n2l_BOUND,EVERY_GENLIST,MAP_ORD_CHR,l2n_append_zeros,l2n_n2l]);

(*---------------------------------------------------------------------------*)
(* Unrolling n2l a fixed number of times.                                    *)
(*---------------------------------------------------------------------------*)

val n2l_bytes_1 = Q.prove
(`!n. n < 256 ==> (n2l 256 n = [n])`,
 rw_tac list_ss [Once n2l_256]);

val n2l_bytes_2 = Q.prove
(`!n. ~(n < 256) /\ n < 65536 ==> 
       (n2l 256 n = [n MOD 256; (n DIV 256) MOD 256])`,
 rw_tac list_ss 
    [Ntimes n2l_256 2, 
     arithmeticTheory.DIV_DIV_DIV_MULT,arithmeticTheory.DIV_LT_X]);

val n2l_bytes_3 = Q.prove
(`!n. ~(n < 65536) /\ n < 256 * 65536 ==> 
       (n2l 256 n = [n MOD 256; 
                     (n DIV 256) MOD 256;
		     (n DIV 65536) MOD 256])`,
 rw_tac list_ss 
    [Ntimes n2l_256 3, 
     arithmeticTheory.DIV_DIV_DIV_MULT,arithmeticTheory.DIV_LT_X]);

val n2l_bytes = save_thm
("n2l_bytes",
 LIST_CONJ [n2l_bytes_1,n2l_bytes_1,n2l_bytes_1]);

(*---------------------------------------------------------------------------*)
(* Encoding nums in expected ranges                                          *)
(*---------------------------------------------------------------------------*)

val enc_1_lem = Q.prove
(`!n. n < 256 ==> (enc 1 n = STRING (CHR n) "")`,
 rw_tac list_ss [enc_def, PAD_RIGHT, GENLIST, layout_def,n2l_bytes_1]);


val enc_2_lem = Q.prove
(`!n. n < 256 * 256 ==> 
      (enc 2 n = STRING (CHR (n MOD 256)) 
                (STRING (CHR ((n DIV 256) MOD 256)) ""))`,
rw_tac list_ss [enc_def, PAD_RIGHT, GENLIST, layout_def,n2l_bytes_2]
 >> Cases_on `n < 256`
 >- rw_tac list_ss [n2l_bytes_1,arithmeticTheory.LESS_DIV_EQ_ZERO]
 >- rw_tac list_ss [n2l_bytes_2]);

val enc_3_lem = Q.prove
(`!n. n < 256 * 256 * 256 ==> 
      (enc 3 n = STRING (CHR (n MOD 256)) 
                (STRING (CHR ((n DIV 256) MOD 256)) 
                (STRING (CHR ((n DIV 65536) MOD 256)) "")))`,
rw_tac list_ss [enc_def, PAD_RIGHT, GENLIST, layout_def]
 >> Cases_on `n < 256`
 >- rw_tac list_ss [n2l_bytes_1,arithmeticTheory.LESS_DIV_EQ_ZERO]
 >- (Cases_on `n < 65536`
     >- rw_tac list_ss [n2l_bytes_2,arithmeticTheory.LESS_DIV_EQ_ZERO]
     >- rw_tac list_ss [n2l_bytes_3]));

val enc_bytes = save_thm
("enc_bytes",
 LIST_CONJ [enc_1_lem,enc_2_lem,enc_3_lem]);


(*---------------------------------------------------------------------------*)
(* Lower bounds on encoding lengths                                          *)
(*---------------------------------------------------------------------------*)

val lower_enc = Q.store_thm
("lower_enc",
 `!w n. w <= LENGTH (enc w n)`,
 rw_tac list_ss [enc_def,layout_def, PAD_RIGHT]);

(*---------------------------------------------------------------------------*)
(* Mapping from string lengths to character lists with abstract contents     *)
(*---------------------------------------------------------------------------*)

val STRLEN_EQ_1 = Q.prove
(`!s. (STRLEN s = 1) <=> ?c. s = STRING c ""`,
 Induct >> rw_tac list_ss []);

val STRLEN_EQ_2 = Q.prove
(`!s. (STRLEN s = 2) <=> ?c1 c2. s = STRING c1 (STRING c2 "")`,
 Cases >> rw_tac list_ss []
       >> Cases_on `t` >> rw_tac list_ss []);

val STRLEN_EQ_3 = Q.prove 
(`!s. (STRLEN s = 3) <=> ?c1 c2 c3. s = [c1; c2 ; c3]`, 
 Cases >> rw_tac list_ss [] 
       >> Cases_on `t` >> rw_tac list_ss []
       >> Cases_on `t'` >> rw_tac list_ss [] 
       >> Cases_on `t` >> rw_tac list_ss []);

val strlen_eq = save_thm
("strlen_eq",
 LIST_CONJ [STRLEN_EQ_1,STRLEN_EQ_2,STRLEN_EQ_3]);

(*---------------------------------------------------------------------------*)
(* Signed numbers                                                            *)
(*                                                                           *)
(* To encode integer i, we map it to n:num by using 2s complement. Then we   *)
(* use l2n o n2l to get back out to 2s complement num, and then we reverse   *)
(* the 2s complement map.                                                    *)
(*                                                                           *)
(* At the word level, 2s complement is "flip each bit and add 1". But it is  *)
(* also implementable purely at the level of ints and nums since an n-bit    *)
(* integer added to its 2s complement equals 2^n (with appropriate coercions *)
(* tossed in to map back and forth between ints and nums).                   *)
(*---------------------------------------------------------------------------*)
(*
 ML version:

  fun n2l n = if n < 256 then [n] else (n mod 256) :: n2l (n div 256);

  fun l2n [] = 0
    | l2n (h::t) = h + 256 * l2n t;

  fun i2n bits i = if 0 <= i then i else MiscLib.exp 2 bits + i;

  fun n2i bits n = 
    if n < MiscLib.exp 2 (bits -1) then n 
    else ~(MiscLib.exp 2 bits - n);

  fun ntest n = (n = l2n (n2l n));
  List.all (equal true) (map ntest (upto 0 65537));

  fun itest bits i = (i = n2i bits (l2n (n2l (i2n bits i))));
  List.all (equal true) (map (itest 16) (upto ~32768 32767));

*)

open integerTheory;
val int_ss = intLib.int_ss;

val i2n_def =
 Define
  `i2n bits (i:int) =
      if 0i <= i then Num(i) else (2 ** bits) - Num(ABS(i))`;

val n2i_def =
 Define
  `n2i bits n = 
     if n < 2 ** (bits - 1n)
      then int_of_num n
     else  -(&(2 ** bits - n))`;

(*
EVAL ``i2n 8n 13i``;
EVAL ``i2n 8n (-13i)``;

EVAL ``n2i 8n (i2n 8n 13i)``;
EVAL ``n2i 8n (i2n 8n (-13i))``;
*)

val enci_def = Define `enci w i = enc w (i2n (8n * w) i)`
val deci_def = Define `deci w s = n2i (8*w) (dec s)`;

val lem = Q.prove
(`!x i:int. i < 0 /\ x < Num (ABS i) /\ -(&x) <= i ==> F`,
 rw_tac int_ss [INT_ABS] >> intLib.ARITH_TAC);

val invertible = Q.prove
(`!bits i. 
   0n < bits /\
  -(&(2n ** (bits - 1))) <= i /\ i < &(2n ** (bits - 1))
  ==>
   (n2i bits (i2n bits i) = i)`,
rw_tac int_ss [n2i_def, i2n_def]
 >> full_simp_tac int_ss [INT_OF_NUM]
 >- (rw_tac int_ss [INT_ABS]
     >- (qpat_x_assum `~(0 <= i)` kall_tac
         >> qpat_x_assum `i < &(2 ** (bits - 1))` kall_tac
         >> `2n ** bits = 2n ** (bits - 1) + 2n ** (bits - 1)`
             by (Cases_on `bits` >> full_simp_tac arith_ss [EXP])
         >> pop_assum SUBST_ALL_TAC
         >> full_simp_tac arith_ss []
         >> metis_tac [lem])
     >- metis_tac [intLib.ARITH_PROVE ``~(0 <= (i:int)) ==> ~(i < 0) ==> F``])
 >- intLib.ARITH_TAC
 >- intLib.ARITH_TAC
);

val deci_enci = Q.store_thm
("deci_enci",
`!w i. 0 < w /\ 
     -&(2 ** (8 * w − 1)) ≤ i /\ i < &(2 ** (8 * w − 1)) 
   ==> 
   (deci w (enci w i) = i)`,
rw_tac arith_ss [deci_def, enci_def,dec_enc]
 >> match_mp_tac invertible
 >> rw_tac arith_ss []);

val i2n_bounds_1 = Q.prove
(`!i:int. -128i <= i /\ i < 128 ==> i2n 8 i < 256n`,
 rw_tac int_ss [i2n_def] >> intLib.ARITH_TAC);

val i2n_bounds_2 = Q.prove
(`!i:int. -32768i <= i /\ i < 32767 ==> i2n 16 i < 65536n`,
 rw_tac int_ss [i2n_def] >> intLib.ARITH_TAC);

val i2n_bounds_3 = Q.prove
(`!i:int. -8388608i <= i /\ i < 8388607 ==> i2n 24 i < 16777216n`,
 rw_tac int_ss [i2n_def] >> intLib.ARITH_TAC);

val enc_bytes' = SIMP_RULE arith_ss [] enc_bytes;

val enci_byte_1 = Q.prove
(`!i:int. -128i <= i /\ i < 128i ==> ?a. enci 1 i = [a]`,
 rw_tac list_ss [enci_def]
 >> metis_tac [enc_bytes',i2n_bounds_1]);

val enci_byte_2 = Q.prove
(`!i:int. -32768i <= i /\ i < 32767 ==> ?a b. enci 2 i = [a;b]`,
 rw_tac list_ss [enci_def]
 >> metis_tac [enc_bytes',i2n_bounds_2]);

val enci_byte_3 = Q.prove
(`!i:int. -8388608i <= i /\ i < 8388607 ==> ?a b c. enci 3 i = [a;b;c]`,
 rw_tac list_ss [enci_def]
 >> metis_tac [enc_bytes',i2n_bounds_3]);

val enci_bytes = save_thm
 ("enci_bytes",
  LIST_CONJ [enci_byte_1,enci_byte_2,enci_byte_3]);

(*---------------------------------------------------------------------------*)
(* Length directed string destructor                                         *)
(*---------------------------------------------------------------------------*)

val split_at_aux_def =
 Define
     `(split_at_aux 0 s acc = SOME(REVERSE acc,s)) /\
      (split_at_aux (SUC n) s acc = 
        case DEST_STRING s
         of NONE => NONE
          | SOME(c,t) => split_at_aux n t (c::acc))`;

val split_at_def =
    Define `split_at n s = split_at_aux n s []`;

val chop_aux_def =
 Define
  `(chop_aux [] s acc = SOME(REVERSE acc,s)) /\
   (chop_aux (n::t) s acc =
     case split_at n s
      of NONE => NONE
       | SOME(pref,suff) => chop_aux t suff (pref::acc))`;

val chop_def =
 Define
  `chop nlist s =
      case chop_aux nlist s []
       of NONE => NONE
        | SOME(lists, suff) => 
          if suff = ""  then SOME lists else NONE`;
						 
val split_at_aux_some = Q.prove
(`!n s acc racc_pref suff. 
    (split_at_aux n s acc = SOME(racc_pref,suff))
    ==> 
    ?pref. (racc_pref = REVERSE acc ++ pref) /\ 
           (s = pref ++ suff) /\ 
           (LENGTH pref = n)`,
 Induct 
  >> rw_tac list_ss [split_at_aux_def]
  >- (EVERY_CASE_TAC
       >- metis_tac[]
       >- (full_simp_tac list_ss [DEST_STRING_LEMS]
	    >> res_tac
            >> rw_tac list_ss []
            >> qexists_tac `q::pref`
            >> rw_tac list_ss []
            >> metis_tac [STRCAT_EQNS,STRCAT_ASSOC])))
;

val strlen_eq_suc = Q.prove
(`!s n. (STRLEN s = SUC n) ==> ?c t. s = STRING c t`,
 Induct >> rw_tac list_ss [STRLEN_THM]);


val split_at_aux_some_eq = Q.prove
(`!n s acc racc_pref suff. 
    (split_at_aux n s acc = SOME(racc_pref,suff))
    <=> 
    ?pref. (racc_pref = REVERSE acc ++ pref) /\ (s = pref ++ suff) /\ (LENGTH pref = n)`,
 Induct
  >> rw_tac list_ss [split_at_aux_def]
  >- metis_tac[]
  >- (EVERY_CASE_TAC
       >> full_simp_tac list_ss [DEST_STRING_LEMS]
       >> rw_tac list_ss [EQ_IMP_THM]
       >- (qexists_tac `q::pref`
           >> rw_tac list_ss []
           >> metis_tac [STRCAT_EQNS,STRCAT_ASSOC])
       >- (`?c t. pref = STRING c t` by metis_tac [strlen_eq_suc]
                 >> rw_tac list_ss []
                 >> full_simp_tac list_ss []
		 >> rw_tac list_ss []
                 >> metis_tac [STRCAT_EQNS,STRCAT_ASSOC])))
;

val split_at_aux_none = Q.prove
(`!n s acc. 
    (split_at_aux n s acc = NONE) ==> LENGTH s < n`,
 Induct 
  >> rw_tac list_ss [split_at_aux_def]
  >> EVERY_CASE_TAC
  >> full_simp_tac list_ss [DEST_STRING_LEMS]
  >> res_tac
  >> rw_tac list_ss [])
;

val split_at_aux_none_eq = Q.prove
(`!n s acc. 
    (split_at_aux n s acc = NONE) <=> LENGTH s < n`,
 Induct 
  >> rw_tac list_ss [split_at_aux_def]
  >> EVERY_CASE_TAC
  >> full_simp_tac list_ss [DEST_STRING_LEMS]);
;

val split_at_some = Q.prove
(`!n s pref suff.
    (split_at n s = SOME (pref,suff))
    ==> 
    (s = pref ++ suff) /\ (LENGTH pref = n)`,
 rw_tac list_ss [split_at_def]
  >> imp_res_tac split_at_aux_some
  >> rw_tac list_ss []
  >> full_simp_tac list_ss []);

val split_at_some_eq = Q.prove
(`!n s pref suff.
    (split_at n s = SOME (pref,suff))
    <=> 
    (s = pref ++ suff) /\ (LENGTH pref = n)`,
 rw_tac list_ss [split_at_def,split_at_aux_some_eq]);

val split_at_none = Q.prove
(`!n s acc. 
    (split_at n s = NONE) ==> LENGTH s < n`,
metis_tac [split_at_def, split_at_aux_none])

val split_at_none_eq = Q.prove
(`!n s acc. 
    (split_at n s = NONE) <=> LENGTH s < n`,
 metis_tac [split_at_def, split_at_aux_none_eq]);

  
val chop_aux_lem = Q.prove
(`!nlist s acc racc_lists suff.
     (chop_aux nlist s acc = SOME (racc_lists,suff))
     ==> 
     ?lists. (racc_lists = REVERSE acc ++ lists) /\
            (LENGTH racc_lists = LENGTH nlist + LENGTH acc) /\
            LIST_REL (\n list. n = LENGTH list) nlist lists /\
            (s = FLAT lists ++ suff)`,
 Induct
 >- (rw_tac list_ss [chop_aux_def] >> metis_tac [LENGTH_REVERSE])
 >- (rw_tac list_ss [chop_aux_def]
     >> EVERY_CASE_TAC
     >- metis_tac []
     >- (full_simp_tac list_ss [split_at_some_eq]
          >> rw_tac list_ss []
          >> res_tac
          >> rw_tac list_ss []
          >> qexists_tac `[q] ++ lists`
          >> rw_tac list_ss [])))
;

val chop_lem = Q.prove
(`!nlist s lists. 
    (chop nlist s = SOME lists)
    ==> 
    (LENGTH lists = LENGTH nlist) /\
    LIST_REL (\n list. n = LENGTH list) nlist lists /\
    (s = FLAT lists)`,
 rw_tac list_ss [chop_def]
 >> EVERY_CASE_TAC
 >> rw_tac list_ss []
 >> imp_res_tac chop_aux_lem
 >> full_simp_tac list_ss []);


(*---------------------------------------------------------------------------*)
(* Enumerations need bijections between constructors and numbers. Booleans   *)
(* are built in, others are defined on when the AADL package is processed.   *)
(*---------------------------------------------------------------------------*)

val num_of_bool_def =
    Define `(num_of_bool F = 0n) /\ (num_of_bool T = 1n)`;

val bool_of_num_def =
    Define `bool_of_num n = if n = 0n then F else T`

val enc_bool_def =
    Define `enc_bool b = enc 1 (num_of_bool b)`;

val dec_bool_def =
    Define `dec_bool s = bool_of_num(dec s)`;

val dec_enc_bool = Q.store_thm
("dec_enc_bool",
 `!b. dec_bool (enc_bool b) = b`,
 Cases 
 >> rw_tac std_ss 
      [enc_bool_def, dec_bool_def,dec_enc,num_of_bool_def,bool_of_num_def])

val bool_bound = Q.store_thm
("bool_bound",
 `!b. num_of_bool b < 256`,
 Cases >> rw_tac arith_ss [num_of_bool_def]);

val FILTER_CORRECT_def = 
 Define 
   `FILTER_CORRECT s (tm:bool) = tm`;

val _ = export_theory();
