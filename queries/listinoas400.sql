SELECT
CMODAL AS MODELLO,
CARTAL AS ARTICOLO,
MAX(ALP1$) AS COSTO
FROM ABB_DATV3.ANALIS01
WHERE TIPR LIKE 'A' AND VERLAL=1
AND CMODAL='@{modello}'
AND CARTAL='@{articolo}'
GROUP BY CMODAL, CARTAL
