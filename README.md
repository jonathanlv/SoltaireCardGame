# SoltaireCardGame
A cardgame written in Java
Java version:
javac 12.0.2 

Hur är programmet organiserat i stort:
Programmet innehåller 9 stycken klasser där framför allt klassen som representerar ett kort samt alla klasserna som representerar en korthög till stor del utgår från koden i Tim Budds program. Dessa klasser är dock modifierade och utvecklade så att nya funktionaliteter och attributer har kunnat adderas till programmet. Jag har lagt till klasserna CardPanel (som utökar JPanel) samt CardGame (som utvecklar JFrame) och innehåller programmets main-metod. 

En överblick av lösningen kan beskrivas som följande. Konstruktorn till klassen CardGame som utökar JFrame kallas på i main-metoden. CardGame lägger till en JPanel-instans i form av CardPanel samt olika instanser av JButton. CardPanel skapar olika typer av korthögar (CardPile) samt en instans av MouseAdapter ( som implementerar MouseMotionListener ). Korthögarna instansieras genom att ta emot x och y koordinater. Genom superklassens (CardPile) konstruktor instansieras en ny Stack och genom metoden addCard läggs instanser av klassen Card in i korthögarna. 

Vilka är klasserna och deras viktigaste metoder:
Card
Konstruktorn i klassen Card sätter precis som i Tim Budds lösning värden på rank och färg och anger faceup till false. Draw() metoden är modifierad så att gif filerna som ligger i mappen cards ritas upp istället för streckade linjer. Detta sker genom avnvändning av klassen ImageIcon som hämtar filerna. Jag har även lagt till sex stycken metoder varav två stycken är get-metoder för ett korts x och y koordinater. De andra fyra metoderna är moveCard(), setRelativeP(), setPosition() samt faceUpFalse(). De första tre används vid förflyttning av kort och den sista, faceUpToFalse() används i DeckPile-klassens select-metod för att ändra värdet på ett korts faceup-variabel till false. 

CardPile
CardPile är en abstrakt klass och representerar en korthög. Koden utgår till stor del från den i Tim Budds program och konstruktorn sätter värden på x och y variablerna samt instansierar en ny Stack. Jag har lagt till tre integer variabler som används i select() metoden som i sin tur används när användaren klickar på ett kort. Dessa variabler är xLimit, yLimitTableau och yLimitSuitPile och fungerar som gränsvärden för när metoden setPosition() ska kallas på i olika korthögar (det vill säga hur långt bort användaren kan släppa ett kort från en existerande korthög). Metoden display() är modifierad så att drawRect() anropas även om korthögen inte är tom. Jag har även lagt till en loop där nextElement() anropas på en instans av Enumeration så att kortet under kortet i toppen av högen också ritas upp med hjälp av draw().

Metoden select() är modifierad så att logiken finns i den abstrakta klassen för korthög istället för som, i Tim Budds lösning, i klassen subklassen TablePile. Metoden tar emot sex stycken variabler som syftar till att bland annat representera ett enskilts korts position samt för att ge möjlighet att passa in cardPanel instanser av specifika korthögar till metoden. 
DeckPile
DeckPile utökar superklassen CardPile och tar till skillnad från de övriga klasserna för korthögar emot en variabel som heter fixed av typen boolean. Med hjälp av den här variabeln har jag lagt till logik i konstruktorn som avgör hur korthögen ska blandas. Om värdet på denna variabel är true när konstruktorn kallas på i klassen CardPanel tilldelas instansen av Stack ( thePile)  korten utan att de blandas. Om värdet istället är false så exekveras istället koden efter else i DeckPiles konstruktor och korten blandas med hjälp av Random klassen som i Tim Budds kod. 

Metoden select() är också modifierad så att högen discardPile fylls på med kort när den är tom och metoden display() är tillagd för att överskrida metoden i superklassen CardPile. 
DiscardPile
Till skillnad från Tim Budds lösning är metoden select() borttagen i denna klass och istället är metoden addCard modifierad så att metoden setPosition() kallas på till en kort-instans.
SuitPile
Klassen utgår helt från koden i Tim Budds lösning. Konstruktorn tar emot koordinater och kallar på super(). Metoden canTake returnerar en boolean och syftar till att kontrollera om kort kan läggas till i korthögen. Metoden returnerar värdet true om kortinstansen som passas in i metoden är av samma färg som toppkortet i den aktuella högen samt om kortinstansen som passas in är en rankenhet högre än än toppkortet ( förutsatt att korthögen inte är tom ).

TablePile
Klassen utgår från koden i Tim Budds lösning men konstruktron tar emot en instans av DeckPile då vi i konstruktorns logik behöver sätta positionen på översta kortet från en instans av deckPile.
CardPanel
Klassen utökar JPanel. För att undvika “magiska konstanter” som dyker upp utan förklaring deklareras fem stycken variabler av typen integer som används i metoden init() för att kalkylera koordinater vid instansiering av korthögar. Precis som i Tim Budds lösning deklareras en instansvariabel (allPiles[]) för alla korthögar samt fyra stycken instansvariabler för varje korthög. Två variabler av typen boolean (fixedGame och fixedShuffle) deklareras för att styra vilken typ av blanding som ska gälla. Variablen indexOfPile representerar indexvärdet för en korthög.

Metoden init() utgår från koden i Tim Budds lösning men är modifierad. Metoden består av en if sats som kontrollerar ifall värdet på fixedGame är true. Om så är fallet ändras värdet på fixedShuffle till true och denna variabel passas in i konstruktorn till DeckPile. En instans av DeckPile tilldelas allPiles[0] och sedan instansieras resterande högar utifrån allPiles. 
Om värdet på fixedGame istället är false så ändras värdet på fixedShuffle till false och motsvarande operation sker med fixedShuffle = false i konstruktorn till DeckPile.

Metoderna setNewGame() och setFixedGame() är kopplade till actionPerformed() i klassen CardGame och anger värden på variabeln fixedGame och anropar metoden init() och repaint(). Metoden moveCard() tar emot ett kort, x och y vairabler samt ett indexvärde för att anropa metoden moveCard i klassen Card samt repaint(). Metoden controlCard() kontrollerar ifall ett kort kan läggas till på en nya koordinater. 
MouseKeeper
Klassen inleds med variabler som syftar till att representera korthögar och kort som användaren valt med musknappen. Två variabler av typen integer (startX och startY) representerar det valda kortets default-koordinater. Konstruktorn tar emot en CardPanel och anger värdet på cardPanel. Klassen innehåller metoderna mousePressed(), mouseDragged() och mouseReleased(). Dessa metoder kontrollerar och opererar på koordinater som användaren angett via musklick, musdrag och mussläpp.
CardGame
CardGame utökar JFrame och implementerar ActionsListener. Kostruktorn instansierar och lägger till två JPanel instanser,  buttonPanel och cardPanel där cardPanel är av typen CardPanel och adderar denna JPanel till JFrame klassen. Metoderna är actionPerformed() som tar emot ett ActionEvent och utför operationer beronde på värdet av getSource() samt main-metoden som instansierar ett CardGame.
