import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.Gesture.Type;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

class CustomListener extends Listener {
	public Robot robot;
	private int init = 0;
	private int d_x = 0;
	private int d_y = 0;

	// Init
	public void onInit(Controller controller) {
		System.out.println("Initialized");
		init = 0;
	}
	// set
	/*public void onConnect(Controller controller) {
		System.out.println("Connected");
	}
	public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }
	public void onExit(Controller controller) {
        System.out.println("Exited");
        init = 0;
    }*/

	// keyFun
	public void onFrame(Controller controller) {
		try {
			robot = new Robot();
		} catch (Exception e) {
		}
		Frame frame = controller.frame();
		InteractionBox box = frame.interactionBox();
		PointableList pointables = frame.pointables();
		HandList hands = frame.hands();
		FingerList fingers = frame.fingers();
		ToolList tools = frame.tools();

		for (Finger point : fingers) {
			if (point.type() == Finger.Type.TYPE_INDEX) {
				System.out.println(" id: " + point.id() + 
						", X: "+ (int)point.stabilizedTipPosition().getX() + 
						", Y: "+ (int)point.stabilizedTipPosition().getY() + 
						", Z: "+ (int)point.tipPosition().getZ());
				// clickIf
				if (point.tipPosition().getZ() < 0) {
					robot.mousePress(InputEvent.BUTTON1_MASK);
				} else {
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				}

				// maveMouse
				Vector pos = point.stabilizedTipPosition();
				Vector boxPos = box.normalizePoint(pos);
				Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

				int x = (int) (screen.width * boxPos.getX());
				int y = (int) (screen.height - boxPos.getY() * screen.height);
				int m_x = MouseInfo.getPointerInfo().getLocation().x;
				int m_y = MouseInfo.getPointerInfo().getLocation().y;
				
				if(init == 0){
					d_x = m_x - x;
					d_y = m_y - y;
					System.out.println("X : " + x);
					System.out.println("Y : " + y);
					System.out.println("MX : " + m_x);
					System.out.println("MY : " + m_y);
					robot.mouseMove(x + d_x, y + d_y);
					init = 1;
				}
				
				if (x - m_x > Math.abs(10) && y - m_y > Math.abs(10) && init == 1) {}
				else
					robot.mouseMove(x + d_x, y + d_y);

				/*try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		}
	}// onFrame
}

public class LeapMouse {

	public static void main(String[] args) {

		CustomListener lis = new CustomListener();
		Controller controller = new Controller();
		controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
		controller.addListener(lis);

		try {
			System.in.read();
		} catch (Exception e) {}
		
		controller.removeListener(lis);
	}
}