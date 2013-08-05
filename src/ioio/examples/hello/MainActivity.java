package ioio.examples.hello;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;


/**
 * This is the main activity of the HelloIOIO example application.
 * 
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link IOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */
public class MainActivity extends IOIOActivity {
	private ToggleButton button_;
	private static String logtag = "hibot";
	private Button buttonForward;
	private Button buttonBackward;
	
	private boolean motionMoving = false;
	private boolean motionForward = false;
	private boolean motionBackward = false;

	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		button_ = (ToggleButton) findViewById(R.id.button);
		
		buttonForward = (Button)findViewById(R.id.button_forward);
		buttonBackward = (Button)findViewById(R.id.button_backward);
		
		buttonForward.setOnTouchListener(forwardListener);
		buttonBackward.setOnTouchListener(backwardListener);
		
	}
	
	private OnTouchListener forwardListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			
			
			if(event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				
				Log.d(logtag, "on - forward");
				
				// here is where you'd set a flag to move forward
				motionMoving = true;
				motionForward = true;
				motionBackward = false;
				return true;
			} else {
				
				Log.d(logtag, "off - forward");
				
				motionMoving = false;
				motionForward = false;
				motionBackward = false;
				return false;
			}
			
		}
	};
	
	private OnTouchListener backwardListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			Log.d(logtag, "sanity touch forward");
			
			if(event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				Log.d(logtag, "on - backward");
				
				// here is where you'd set a flag to move backward
				motionMoving = true;
				motionForward = false;
				motionBackward = true;
				return true;
			} else {
				Log.d(logtag, "off - backward");
				
				motionMoving = false;
				motionForward = false;
				motionBackward = false;
				return false;
			}
		}
	};

	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		/** The on-board LED. */
		private DigitalOutput led_;
		private DigitalOutput in1;
		private DigitalOutput ena;
		private DigitalOutput in2;
		private DigitalOutput in3;
		private DigitalOutput enb;
		private DigitalOutput in4;

		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			
			Log.d(logtag, "a connection with ioio has been established");
			
			// this is the led pin for the board
			led_ = ioio_.openDigitalOutput(0, true);
			
			in1 = ioio_.openDigitalOutput(18, true); // pin 18
			ena = ioio_.openDigitalOutput(19, true); // pin 19
			in2 = ioio_.openDigitalOutput(20, true); // pin 20
			
			in3 = ioio_.openDigitalOutput(21, true); // pin 21
			enb = ioio_.openDigitalOutput(22, true); // pin 22
			in4 = ioio_.openDigitalOutput(23, true); // pin 23
			
			
			Log.d(logtag, "writing to leds");
			in1.write(true);
			ena.write(true);
			in2.write(true);
			in3.write(true);
			enb.write(true);
			in4.write(true);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Log.d(logtag, e.toString());
			}
			
			Log.d(logtag, "turning off leds");
			in1.write(false);
			ena.write(false);
			in2.write(false);
			in3.write(false);
			enb.write(false);
			in4.write(false);
		}

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		 */
		@Override
		public void loop() throws ConnectionLostException {
			led_.write(!button_.isChecked());
			
			if(motionMoving && button_.isChecked()) {
				
				if(motionForward) {
					
					// forward
					in1.write(false);
					ena.write(true);
					in2.write(true);
					
					in3.write(false);
					enb.write(true);
					in4.write(true);
				
				} else if(motionBackward) {
					
					// backward
					in1.write(true);
					ena.write(true);
					in2.write(false);
					
					in3.write(true);
					enb.write(true);
					in4.write(false);
					
				} else {
					
					// stop
					in1.write(false);
					ena.write(false);
					in2.write(false);
					
					in3.write(false);
					enb.write(false);
					in4.write(false);
					
				}
				
			} else {
				
				// stop
				in1.write(false);
				ena.write(false);
				in2.write(false);
				
				in3.write(false);
				enb.write(false);
				in4.write(false);
				
			}
			
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Log.d(logtag, e.toString());
			}
		}
	}

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
}