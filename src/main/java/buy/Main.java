package buy;

// Mouse trail imports
import utils.MouseTrail;
import utils.MouseCursor;

// Import sleep
import utils.Sleep;

// OSBot imports
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.RS2Widget;
//import org.osbot.rs07.api.ui.Tab;

// Java imports
import java.time.Duration;
import java.awt.Graphics2D;
import java.util.Date;
import java.awt.Color;

@ScriptManifest(author = "Fyoxy", name = "BuyShop", info = "Buys capes and aprons from shop", version = 0.1, logo = "")
public final class Main extends Script  {
    
    private MouseTrail trail = new MouseTrail(0, 255, 255, 2000, this);
    private MouseCursor cursor = new MouseCursor(52, 4, Color.white, this);

    Area shopArea = new Area(
    new int[][]{
        { 3208, 3416 },
        { 3208, 3412 },
        { 3203, 3415 },
        { 3203, 3418 },
        { 3208, 3419 },
        { 3208, 3418 }
    });

    long startTime;
    long elapsedTime = 0L;
    long totalTime = 0L;

    private boolean hop = false;

    @Override
    public final void onStart() {

        // Start timer
        startTime = System.currentTimeMillis();

        log("Cape buyer started");
        Sleep.sleepUntil(() -> getClient().isLoggedIn() && myPlayer().isVisible(), 6000, 500);
    }

    @Override
    public final int onLoop() throws InterruptedException {
        if (!getInventory().contains("Coins")) {
            if (getBank().isOpen()) {
                if (!getBank().contains("Coins")) {
                    stop(true);
                }
                else {
                    getBank().withdrawAll("Coins");
                }
            }
            else {
                if (Banks.VARROCK_WEST.contains(myPosition())) {
                    getBank().open();
                }
                else {
                    getWalking().webWalk(Banks.VARROCK_WEST);
                }
            }
        }
        else if (hop == true && !getInventory().isFull()) {
            if (getStore().isOpen()) {
                getStore().close();
            }
            else {
                log("Hopping");
                if (getWorlds().hopToF2PWorld()) {
                    log("Hopping false");
                    hop = false;
                }
            }
        }
        else if (getInventory().isFull()) {
            if (Banks.VARROCK_WEST.contains(myPosition())) {
                if (getBank().isOpen()) {
                    getBank().depositAllExcept("Coins");
                }
                if (getBank() != null && getBank().open()) {
                    Sleep.sleepUntil(() -> getBank().isOpen(), 5000);
                }
            }
            else {
                getWalking().webWalk(Banks.VARROCK_WEST);
            }
        }
        else {
            NPC shop = getNpcs().closest( npc -> npc.getName().equals( "Thessalia" ) && getMap().canReach( npc ));
            if ( shop != null) {
                if (getStore().isOpen()) {
                    if (getStore().getAmount("Red cape") == 0 && getStore().getAmount("Brown apron") == 0) {
                        hop = true;
                    }
                    else {
                        
                        if ( getStore().getAmount("Brown apron") > 0) {
                            Sleep.sleepUntil(() -> getStore().buy("Brown apron", 1) || getInventory().isFull(), 5000);
                        }
                        if ( getStore().getAmount("Red cape") > 0) {
                            RS2Widget red = getWidgets().get(300, 16, 9);
                            if (red != null) {
                                red.interact("Buy 50");
                                Sleep.sleepUntil(() -> getStore().getAmount("Red cape") == 0 || getInventory().isFull(), 5000);
                            }
                                
                            
                        }
                            
                    }
                }
                else {
                    shop.interact("Trade");
                    Sleep.sleepUntil(() -> getStore().isOpen(), 5000, 600);
                }
            }
            else {
                getWalking().webWalk(shopArea);
            }
        }
        
        
        return random( 100, 300 ); // Sleep in MS for onLoop to be called again
    }


    @Override
    public final void onMessage(final Message message) {
        log("A message arrived in the chatbox: " + message.getMessage());
    }


    @Override
    public void onPaint(final Graphics2D g) {

        elapsedTime = (new Date()).getTime() - startTime;
        totalTime = (new Date()).getTime() - startTime;

        g.drawString( timeInHMS(elapsedTime), 10, 30 );
        g.drawString( timeInHMS(totalTime), 10, 60 );

        trail.paint(g);
	    cursor.paint(g);
    }


    @Override
    public final void onExit() {
        log("Script exit");
    }

    public String timeInHMS( long timeInMS ) {

        Duration duration = Duration.ofMillis(elapsedTime);
        long seconds = duration.getSeconds();

        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;

        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }
}