
package card.types;

import card.Card;
import static card.Card.RATIO;
import enums.Knowledge;
import static enums.Phase.MAIN;
import game.ClientGame;
import game.Game;
import helpers.Hashmap;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import static javax.imageio.ImageIO.read;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

/**
 * Abstract class for the Item card type.
 * @author pseudo
 */
public abstract class Passive extends Card {
    
    /**
     *
     * @param name
     * @param cost
     * @param knowledge
     * @param text
     * @param image
     * @param owner
     */
    public Passive(String name, int cost, Hashmap<Knowledge, Integer> knowledge, String text, String image, String owner) {
        super(name, cost, knowledge, text, "assets/passive.png", owner);
    }
    
    
    @Override
    public void resolve(Game game) {
        game.deplete(id);
        game.players.get(owner).inPlayCards.add(this);
    }
    
    @Override
    public boolean canPlay(ClientGame game){ 
        return (game.player.energy >= cost)
               && game.player.hasKnowledge(knowledge)
               && game.turnPlayer.equals(owner)
               && game.phase == MAIN;
    }  
    
    @Override
    public void updatePanel() {
        getPanel().removeAll();
        getPanel().setLayout(new MigLayout("wrap 1"));
        getPanel().setPreferredSize(new Dimension(150, (int) (150 * RATIO)));
        getPanel().add(new JLabel(cost + " " + getKnowledgeString()));
        getPanel().add(new JLabel("<html>" + name + "</html>"));
        try {
            getPanel().add(new JLabel(new ImageIcon(read(new File(image)).getScaledInstance(100, -1, 0))));
        } catch (IOException ex) {
        }
        getPanel().add(new JLabel("Passive"));
        JLabel textLabel = new JLabel("<html>"+ text + "</html>");
        getPanel().add(textLabel);
        drawCounters();
        drawBorders();
        setToolTip();
        getPanel().setBackground(getColor());
        getPanel().setVisible(true);
        getPanel().revalidate();
        getPanel().repaint();
    }
}