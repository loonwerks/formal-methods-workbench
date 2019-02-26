(*---------------------------------------------------------------------------*)
(* Maps from Json representation of AADL to AST and then to HOL. Solely      *)
(* aimed at extracting filter properties, plus support definitions.          *)
(*---------------------------------------------------------------------------*)

open Lib Feedback HolKernel boolLib MiscLib AADL;

val ERR = Feedback.mk_HOL_ERR "splat";

(*---------------------------------------------------------------------------*)
(* App-ify                                                                   *)
(*---------------------------------------------------------------------------*)

fun failwithERR e =
  (stdErr_print (Feedback.exn_to_string e); MiscLib.fail());

fun parse_args args =
 let fun printHelp() = stdErr_print
          ("Usage: splat [-dfagen (HOL | SML)] <name>.json\n")
     fun fail() = (printHelp(); MiscLib.fail())
     fun checkFile s =
       let val filename = FileSys.fullPath s
                handle e => (stdErr_print (Feedback.exn_to_string e); fail())
       in case String.tokens (equal #".") filename
           of [file,"json"] => s
            | otherwise => fail()
       end
 in case args
     of [jsonfile] => checkFile jsonfile
      | otherwise => fail()
 end

fun top_pkg_name [] = (stdErr_print "No packages found ... exiting\n"; MiscLib.fail())
  | top_pkg_name ((pkgName,_)::t) = pkgName;

fun filters_of (a,b,c,d) = d

fun by_fiat_tac g = ACCEPT_TAC (mk_thm([],snd g)) g;

fun prove_filter_props {name,regexp,encode_def,decode_def,
                        inversion, correctness, implicit_constraints} =
 let in
     store_thm(name^"inversion",inversion,by_fiat_tac);
     store_thm(name^"correctness",correctness,by_fiat_tac);
     ()
 end;

fun main () =
 let val _ = stdErr_print "splat: \n"
     val jsonfile = parse_args(CommandLine.arguments())
     val ([jpkg],ss) = apply_with_chatter Json.fromFile jsonfile
	   ("Parsing "^jsonfile^" ... ") "succeeded.\n"
     val pkgs = apply_with_chatter AADL.scrape_pkgs jpkg
	   ("Converting Json to AST ... ") "succeeded.\n"
     val thyName = top_pkg_name pkgs
     val _ = new_theory thyName
     val logic_defs = apply_with_chatter (pkgs2hol thyName) pkgs
	   ("Converting AST to logic ...\n") "---> succeeded.\n"
     val filter_spec_thms = filters_of logic_defs
     val filter_defs_and_props = apply_with_chatter 
           (List.map splatLib.filter_correctness) filter_spec_thms
	   ("Constructing filters and proving filter properties ...\n") 
           "---> succeeded.\n"
     val _ = List.app prove_filter_props filter_defs_and_props
  in 
      Theory.export_theory()
    ; stdErr_print "Finished.\n"
    ; MiscLib.succeed()
 end;
