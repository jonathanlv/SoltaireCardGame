/*
Jonathan Vogel
jonathan.vogel@hotmail.com
*/

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Stack;
import java.util.EmptyStackException;
import java.util.Random;

class Card{ 
    
    final private static ImageIcon Img = new ImageIcon("cards/b1fv.gif");
    final public static int width = Img.getIconWidth();
    final public static int height = Img.getIconHeight();

    final private static int heart = 0;
    final private static int spade = 1;
    final private static int diamond = 2;
    final private static int club = 3;
    private boolean faceup;
    private int r;
    private int s;

    private int x;
    private int y;
    private int relX, relY;


    Card (int sv, int rv) {
        s = sv;
        r = rv;
        faceup = false;
    }

    // access attributes of card
    public int rank () { return r; }

    public int suit() { return s; }

    public boolean faceUp()	{ return faceup; }

    public void flip() {
        faceup = ! faceup;
    }

    public Color color() {
        if (faceUp())
            if (suit() == heart || suit() == diamond)
                return Color.red;
            else
                return Color.black;
        return Color.yellow;
    }

    public void draw (Graphics g) {

        String rankArray[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "q", "k"};
        g.clearRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // draw body of card
        g.setColor(color());

        if (faceUp()) {
            String rankString = rankArray[rank()];

            String firstLetter = "";

            if (this.suit() == heart)
                firstLetter = "h";
            else if (this.suit() == spade)
                firstLetter = "s";
            else if (this.suit() == diamond)
                firstLetter = "d";
            else if (this.suit() == club)
                firstLetter = "c";

            String fileName = firstLetter+rankString+".gif";

            ImageIcon cardImg = new ImageIcon(this.getClass().getResource("cards/"+fileName));

            g.drawImage(cardImg.getImage(), this.x, this.y, null);

        } else { 
            ImageIcon cardImg = new ImageIcon(this.getClass().getResource("cards/b1fv.gif"));
            g.drawImage(cardImg.getImage(), x, y, null);
        }
    }

    public void moveCard(int newX, int newY) {
        x = newX - relX;
        y = newY - relY;
    }

    public void setRelativeP(int x0, int y0) {
        relX = x0 - x;
        relY = y0 - y;
    }

    public void setPosition(int x0, int y0) {
        x = x0;
        y = y0;
    }

    public void faceUpFalse() {
        faceup = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}

abstract class CardPile {
    // coordinates of the card pile
    protected int x;
    protected int y;
    protected Stack thePile;

    private final int xLimit = 20;
    private final int yLimitTableau = 45;   
    private final int yLimitSuitPile = 10;   

    // constructor
    CardPile (int xl, int yl) {
        x = xl;
        y = yl;
        thePile = new Stack();
    }

    // access to cards are not overridden
    public final Card top() {
        return (Card) thePile.peek();
    }

    public final boolean isEmpty() {
        return thePile.empty();
    }

    public final Card pop() {
        try {
            return (Card) thePile.pop();
        } catch (EmptyStackException e) { return null; }
    }

    public void addCard (Card aCard) {
        thePile.push(aCard);
    }

    public boolean includes (int tx, int ty) {
        if (!isEmpty()) {
            int xTop = this.top().getX();
            int yTop = this.top().getY();
            return xTop <= tx && tx <= xTop + Card.width && yTop <= ty && ty <= yTop + Card.height;
        }
        return x <= tx && tx <= x + Card.width && y <= ty && ty <= y + Card.height;
    }

    public void display (Graphics g) {
        
        g.setColor(Color.BLACK);
        g.drawRect(x, y, Card.width, Card.height);

        for (Enumeration e = thePile.elements(); e.hasMoreElements(); ) {
            Card aCard = (Card) e.nextElement();
            aCard.draw(g);
        }
    }


    public void select (int tx, int ty, int xStart, int yStart, SuitPile[] suitPiles, TablePile[] tablePiles) {

        if (isEmpty())
            return;

        Card topCard = pop();

        for (int i = 0; i < 4; i++) {
            if (suitPiles[i].canTake(topCard)) {

                int x = suitPiles[i].x;
                int y = suitPiles[i].y;

                if ((x - xLimit <= tx && tx <= x + xLimit && y - yLimitSuitPile <= ty && ty <= y + yLimitSuitPile)) {
                    topCard.setPosition(tx, ty);
                    suitPiles[i].addCard(topCard);
                    return;

                }
            }
        }

        for (int i = 0; i < 7; i++)
            if (tablePiles[i].canTake(topCard) ) {

                int x = tablePiles[i].x;
                int y;

                if (!tablePiles[i].isEmpty()) {
                    y = tablePiles[i].top().getY();
                } else {
                    y =  tablePiles[i].y;
                }

                
                if (x - xLimit <= tx && tx <= x + xLimit && y <= ty && ty <= y + yLimitTableau) {
                    topCard.setPosition(tx, ty);
                    tablePiles[i].addCard(topCard);
                    return;
                }
            }

        topCard.setPosition(xStart, yStart);
        addCard(topCard);

    }

}

class DeckPile extends CardPile {

    private boolean fixed;

    DeckPile(int x, int y, boolean fixed){
        // first initialize parent
        super(x, y);
        
        this.fixed = fixed;

        if(fixed){

            for (int i = 0; i < 4; i++)
            for (int j = 0; j <= 12; j++) {

                Card card = new Card(i, j);

                card.setPosition(x, y);

                thePile.push(card);
            }

        }else{

            for (int i = 0; i < 4; i++)
            for (int j = 0; j <= 12; j++) {

                Card card = new Card(i, j);

                card.setPosition(x, y);

                thePile.push(card);
            }

             // then shuffle the cards
        Random generator = new Random();
        for (int i = 0; i < 52; i++) {
            int j = Math.abs(generator.nextInt() % 52);
            // swap the two card values
            Object temp = thePile.elementAt(i);
            thePile.setElementAt(thePile.elementAt(j), i);
            thePile.setElementAt(temp, j);

        }

        }
    }
  
    public void select(DiscardPile discardPile) {
        if (isEmpty()) {
            while (!discardPile.isEmpty()) {
                Card topCard = discardPile.pop();
                topCard.faceUpFalse();
                topCard.setPosition(this.x, this.y);
                addCard(topCard);
            }
        } else {
            Card card = pop();
            card.setPosition(discardPile.x, discardPile.y);
            discardPile.addCard(card);
        }
    }
    public Card getDeckPilePop(){
        Card card = pop();
        return card;
    }

    @Override
    public void display (Graphics g) {
        g.setColor(Color.blue);
        if (isEmpty())
            g.drawRect(x, y, Card.width, Card.height);
        else {
            top().draw(g);
        }
    }

}

class DiscardPile extends CardPile {

    DiscardPile(int x, int y) {
        super(x, y);
    }

    public void addCard(Card aCard) {
        if (!aCard.faceUp())
            aCard.flip();
        aCard.setPosition(this.x, this.y);
        super.addCard(aCard);
    }
}

class SuitPile extends CardPile {

    SuitPile(int x, int y) {
        super(x, y);
    }

    public boolean canTake(Card aCard) {
        if (isEmpty())
            return aCard.rank() == 0;
        Card topCard = top();
        return (aCard.suit() == topCard.suit()) && (aCard.rank() == 1 + topCard.rank());
    }

}

class TablePile extends CardPile {
    

    TablePile(int x, int y, int c, DeckPile deckPile) { 
        // initialize the parent class
        super(x, y);

        // then initialize our pile of cards
        for (int i = 0; i < c; i++) {
            Card aCard = deckPile.pop();
         
            aCard.setPosition(x, y+Card.height*i/CardPanel.DIVISOR);
            addCard(aCard);
        }
        // flip topmost card face up
        top().flip();
    }
   

    public boolean canTake(Card aCard) {
        if (isEmpty())
            return aCard.rank() == 12;
        Card topCard = top();
        return (aCard.color() != topCard.color()) && (aCard.rank() == topCard.rank() - 1);
    }

}

class CardPanel extends JPanel {

    public DeckPile deckPile;
    public DiscardPile discardPile;
    private TablePile tableau[];
    private SuitPile suitPile[];
    public CardPile allPiles[];

    private final int REL_CARD_SPACE = 40; 
    private final int LEFT_SPACE = 60; 
    private final int TOP_SPACE = 30; 
    final static int DIVISOR = 4;
    private final int CARD_SPACE = Card.height / DIVISOR;  

    private boolean fixedGame = false;
    private boolean fixedShuffle = false;
    private int indexOfPile = -1;

    CardPanel() {

        init();

        MouseKeeper mouseKeeper = new MouseKeeper(this);
        addMouseListener(mouseKeeper);
        addMouseMotionListener(mouseKeeper);

        setBackground(Color.GREEN);
        setVisible(true);

    }
    private void init()  {

        if (fixedGame) {
            fixedShuffle = true;
            allPiles = new CardPile[13];
            suitPile = new SuitPile[4];
            tableau = new TablePile[7];
            allPiles[0] = deckPile = new DeckPile(LEFT_SPACE + 6*Card.width + 6* REL_CARD_SPACE, TOP_SPACE, fixedShuffle);
        } else {

            fixedShuffle = false;
            // first allocate the arrays
            allPiles = new CardPile[13];
            suitPile = new SuitPile[4];
            tableau = new TablePile[7];
            // then fill them in
            allPiles[0] = deckPile = new DeckPile(LEFT_SPACE + 6*Card.width + 6* REL_CARD_SPACE, TOP_SPACE, fixedShuffle);
        }
        allPiles[1] = discardPile = new DiscardPile(LEFT_SPACE + 5*Card.width + 5*REL_CARD_SPACE, TOP_SPACE);
        for (int i = 0; i < 4; i++)
            allPiles[2 + i] = suitPile[i] =
                    new SuitPile(LEFT_SPACE + (Card.width + REL_CARD_SPACE) * i, TOP_SPACE);
        for (int i = 0; i < 7; i++)
            allPiles[6 + i] = tableau[i] =
                    new TablePile(LEFT_SPACE + (Card.width + REL_CARD_SPACE) * i,
                            TOP_SPACE + Card.height + CARD_SPACE, i + 1, deckPile); 
    }


    public void paint(Graphics g) {
        for (int i = 0; i < 13; i++)
            allPiles[i].display(g);

        if (0<= indexOfPile && indexOfPile <13)
            allPiles[indexOfPile].display(g);

    }

    public void setNewGame()  {
        fixedGame = false;
        init();
        repaint();
    }
    public void setFixedGame()  {
        fixedGame = true;
        init();
        repaint();
    }

    public void moveCard(Card card, int x, int y, int i) {
        card.moveCard(x, y);
        indexOfPile = i;
        repaint();
    }

   

    public void controlCard(Card card, CardPile cardPile, int xx, int yy) {

        if (card != null) {
            int cardX = card.getX();
            int cardY = card.getY();

            cardPile.select(cardX, cardY, xx, yy, suitPile, tableau);
        }

        repaint();
    }

}


class MouseKeeper extends MouseAdapter implements MouseMotionListener {

    private CardPanel cardPanel;
    private CardPile chosenPile;
    private int chosenPileIndex = -1;
    private Card chosenCard;
    
    private int startX = 0;
    private int startY = 0;

    MouseKeeper(CardPanel s) {
        cardPanel = s;
    }

    public void mousePressed(MouseEvent e) {

        int eX = e.getX();
        int eY = e.getY();

        for (int i = 0; i < 13; i++) {

            if (cardPanel.allPiles[i].includes(eX, eY)) {

                if (i==0) {
                    cardPanel.deckPile.select(cardPanel.discardPile);
                } else {
                    chosenPile = cardPanel.allPiles[i];
                    chosenPileIndex = i;
                    if (!chosenPile.isEmpty()) {
                        chosenCard = cardPanel.allPiles[i].top();
                        startX = chosenCard.getX();
                        startY = chosenCard.getY();
                        chosenCard.setRelativeP(eX, eY);
                    }
                }

            }
        }

    }

    public void mouseDragged(MouseEvent e) {

        int eX = e.getX();
        int eY = e.getY();

        if (chosenCard!=null && chosenCard.faceUp()){
            cardPanel.moveCard(chosenCard, eX, eY, chosenPileIndex);
        }

    }

    public void mouseReleased(MouseEvent e) {
        if (chosenCard!= null && !chosenCard.faceUp())
            chosenCard.flip();

        cardPanel.controlCard(chosenCard, chosenPile, startX, startY);
        chosenPile = null;
        chosenCard = null;
        chosenPileIndex = -1;
    }
}


public class CardGame extends JFrame implements ActionListener {

    private CardPanel cardPanel;
    private JButton newGameButton = new JButton("New Game");
    private JButton fixedButton = new JButton("Fixed Game");
    public CardGame() {

        cardPanel = new CardPanel();

        add(cardPanel);
        getContentPane().setBackground(Color.GREEN);

        newGameButton.addActionListener(this);
        fixedButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.GREEN);
        buttonPanel.add(fixedButton);
        buttonPanel.add(newGameButton);

        add("South", buttonPanel);

        setSize(900, 500);
        setTitle("Card Game");
        setLocation(100, 100); 
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
  
            if (e.getSource() == newGameButton) {
                cardPanel.setNewGame();
            } else if (e.getSource() == fixedButton) {
                cardPanel.setFixedGame();
            }
     
    }

    public static void main(String[] args)  {
        new CardGame();
    }
}





