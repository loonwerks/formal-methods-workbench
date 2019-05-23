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
          ("Usage: splat <name>.json\n")
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

fun shortcut g = ACCEPT_TAC (mk_thm([],snd g)) g;

fun prove_filter_props {name,regexp,encode_def,decode_def,
                        inversion, correctness, receiver_correctness, implicit_constraints} =
 let in
     store_thm(name^"_inversion",inversion,shortcut);
     store_thm(name^"_correctness",correctness,shortcut);
     store_thm(name^"_receiver_correctness",receiver_correctness,shortcut);
     ()
 end;

fun main () =
 let val _ = stdErr_print "splat: \n"
     val _ = stdErr_print (String.concat ["working directory is ", FileSys.getDir(), "\n"])
     val jsonfile = parse_args(CommandLine.arguments())
     val ([jpkg],ss) = apply_with_chatter Json.fromFile jsonfile
	   ("Parsing "^jsonfile^" ... ") "succeeded.\n"
     val pkgs = apply_with_chatter AADL.scrape_pkgs jpkg
	   ("Converting Json to AST ... ") "succeeded.\n"
     val thyName = fst (last pkgs)
     val _ = new_theory thyName
     val logic_defs = apply_with_chatter (pkgs2hol thyName) pkgs
	   ("Converting AST to logic ...\n") "---> succeeded.\n"
     fun filters_of (a,b,c,d) = d
     val filter_spec_thms = filters_of logic_defs
     val filter_defs_and_props = apply_with_chatter 
           (List.map splatLib.filter_correctness) filter_spec_thms
	   ("Constructing filters, encoders, decoders, and properties ... ") 
           "succeeded.\n"
     val _ = apply_with_chatter (List.app prove_filter_props) filter_defs_and_props
	   ("Proving filter properties ... ") "succeeded.\n"
  in 
      Theory.export_theory()
    ; stdErr_print "Finished.\n"
    ; MiscLib.succeed()
 end;
