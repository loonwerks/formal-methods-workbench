(*---------------------------------------------------------------------------*)
(* Maps from Json representation of AADL to AST and then to HOL. Solely      *)
(* aimed at extracting filter properties, plus support definitions.          *)
(*---------------------------------------------------------------------------*)

open Lib Feedback HolKernel boolLib MiscLib regexpLib AADL;

val justifyDefault = regexpLib.SML;

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
     of ["-dfagen","SML",jsonfile] => (regexpLib.SML,checkFile jsonfile)
      | ["-dfagen","HOL",jsonfile] => (regexpLib.HOL,checkFile jsonfile)
      | [jsonfile]                 => (justifyDefault,checkFile jsonfile)
      | otherwise => fail()
 end

fun main () =
 let val _ = stdErr_print "splat: \n"
     val (justify,jsonfile) = parse_args(CommandLine.arguments())
     val ([jpkg],ss) = 
        apply_with_chatter Json.fromFile jsonfile
	   ("Parsing "^jsonfile^" ... ") "succeeded.\n"
     val (pkgName,enums,recds,fns,filters) = 
        apply_with_chatter AADL.get_pkg_decls jpkg
	   ("Converting Json to AST ...") "succeeded.\n"
  in 
    apply_with_chatter AADL.gen_hol (pkgName,enums,recds,fns,filters)
	   ("Generating HOL theory ...") "splat run succeeded.\n"
 end;
