SELECT 
MDATBO AS data,
MNUMBO AS numero,
MENTEN AS ente_numerazione,
MCODNU AS codice_numerazione,
MCODCL AS codice_cliente,
MCLIDI AS tipo_magazzino,
MCODMA AS codice_magazzino,
MCAUSA AS causale,
MSTAGI AS stagione,
MMODEL AS modello,
MARTIC AS articolo,
MCOLOR AS colore,
MSCALA AS scalarino,
MDM1 AS qta1,
MDM2 AS qta2,
MDM3 AS qta3,
MDM4 AS qta4,
MDM5 AS qta5,
MDM6 AS qta6,
MDM7 AS qta7,
MDM8 AS qta8,
MDM9 AS qta9,
MDM10 AS qta10,
MDM11 AS qta11,
MDM12 AS qta12,
MPRZ$1 AS prezzo
FROM ABB_DATV3.ARCMOD
WHERE MTIPRE='A' AND MDATBO='@{data}' AND MNUMBO='@{numero}' AND MENTEN='@{enteNumerazione}' AND MCODNU='@{codiceNumerazione}'
