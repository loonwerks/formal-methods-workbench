(* use "provers.ml"; open provers;  *)

val _ = new_theory "gps";

val _ =
 Hol_datatype
   `gps = <| lat:  int ;
             lon : int ;
             alt : int |>`;

val good_gps_def =
  Define
    `good_gps recd <=>
         -90 <= recd.lat /\ recd.lat <= 90 /\
         -180 <= recd.lon /\ recd.lon <= 180 /\
         0 <= recd.alt /\ recd.alt <= 17999`;

(*---------------------------------------------------------------------------*)
(* Encode/decode gps elts                                                    *)
(*---------------------------------------------------------------------------*)

val encode_def =
    Define
    `encode recd =
       CONCAT [encZ 1 recd.lat;
               encZ 1 recd.lon;
               encZ 2 recd.alt]`;

(*---------------------------------------------------------------------------*)
(* Note that decZ is total.                                                  *)
(*---------------------------------------------------------------------------*)

val decode_def =
 Define
  `decode s =
    case s
     of [a;b;c;d;e;f;g] =>
        SOME <| lat := decZ [a;b];
                lon := decZ [c;d];
                alt := decZ [e;f;g] |>
      | otherwise => NONE`;

(*---------------------------------------------------------------------------*)
(* Round trip theorem                                                        *)
(*---------------------------------------------------------------------------*)

Theorem decode_encode :
  !m. good_gps m ==> (decode (encode m) = SOME m)
Proof
  ROUND_TRIP_TAC (encode_def, decode_def,good_gps_def)
QED

(*---------------------------------------------------------------------------*)
(* Regexp expressing the interval constraints                                *)
(*---------------------------------------------------------------------------*)

val gps_regexp =
    Regexp_Match.normalize
        (Regexp_Type.fromQuote `\i{~90,90}\i{~180,180}\i{0,17999}`);

val gps_regexp_term = regexpSyntax.regexp_to_term gps_regexp;

(*---------------------------------------------------------------------------*)
(* Compile regexp to a DFA                                                   *)
(*---------------------------------------------------------------------------*)

regexpLib.matcher regexpLib.SML gps_regexp;


(*---------------------------------------------------------------------------*)
(* Correctness theorem                                                       *)
(*---------------------------------------------------------------------------*)

Theorem AGREE_ENCODE_THM :
  !m. good_gps m <=> encode(m) IN regexp_lang ^gps_regexp_term
Proof
  AGREE_ENCODE_TAC (encode_def, decode_def, good_gps_def)
QED

(*---------------------------------------------------------------------------*)
(* Another correctness theorem                                               *)
(*---------------------------------------------------------------------------*)

Theorem AGREE_DECODE_THM :
 !s. s IN regexp_lang ^gps_regexp_term ==> good_gps (THE (decode s))
Proof
  AGREE_DECODE_TAC (encode_def, decode_def, AGREE_ENCODE_THM)
QED
