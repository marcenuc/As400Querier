SELECT 
MCODCL AS CODICE_CLIENTE,
MCLIDI AS TIPO_MAGAZZINO,
MCODMA AS CODICE_MAGAZZINO,
MCAUSA AS CAUSALE,
MSTAGI AS STAGIONE,
MMODEL AS MODELLO,
MARTIC AS ARTICOLO,
MCOLOR AS COLORE,
MSCALA AS SCALARINO,
MDM1 AS QTA1,
MDM2 AS QTA2,
MDM3 AS QTA3,
MDM4 AS QTA4,
MDM5 AS QTA5,
MDM6 AS QTA6,
MDM7 AS QTA7,
MDM8 AS QTA8,
MDM9 AS QTA9,
MDM10 AS QTA10,
MDM11 AS QTA11,
MDM12 AS QTA12,
MPRZ$1 AS PREZZO
FROM ABB_DATV3.ARCMOD
WHERE MTIPRE='A' AND MDATBO='@{data}' AND MNUMBO='@{numero}' AND MENTEN='@{enteNumerazione}' AND MCODNU='@{codiceNumerazione}'
