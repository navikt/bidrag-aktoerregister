# Bidrag-Aktoerregister 

Applikasjonen har ansvar for å holde oversikt over endringer i navn, adresse og kontonummer for aktører involvert i bidragssaker. Utover personer kan slike aktører være blant annet kommuner, institusjoner, spesifike avdelinger innenfor en oranisasjon, utlandske myndigheter eller sperrede bankkontoer.

For personer er det kun opplysninger om kontonummer som følges opp, men for de andre typene aktører følges også navn og adresse. Informasjonen om aktørene kan hentes ved kall med identtype og ident.

## Hendelse-API
I stedet for at konsumentene skal spørre om opplysningene for hver enkelt aktør jevnlig er det lagt opp et hendelses-API. En hendelse inneholder et sekvensnummer og en aktørId. Ved kall til hendelse-APIet sendes det med første sekvensnummer som ønskes og maksimalt antall hendelser.

Sekvensen i hendelses-strømmen vil alltid være stigende, men det kan forekomme hull i rekken. Spørres det etter et sekvensnummer som ikke eksisteres får man det neste i stedet, samt de etterfølgende hendelsene inntil det ikke er flere hendelser igjen eller maksimalt antall er nådd. Dersom det ikke finnes noen hendelser med etterspurt sekvensnummer eller høyere returneres en tom liste.

Konsumenten er selv ansvarlig for å huske hvilket sekvensnummer som skal hentes ut som det neste.

Hendelsene inneholder i seg selv ikke endringene, disse må hentes for aktørId'n dersom det er interessant for konsumenten.

