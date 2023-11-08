/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package processing.app;

import cc.arduino.packages.BoardPort;
import processing.app.legacy.PApplet;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JButton;

import static processing.app.I18n.tr;

@SuppressWarnings("serial")
public class SerialMonitor extends AbstractTextMonitor {

  private Serial serial;
  private int serialRate;

  private boolean connectionDisabled = false;

  private static final int COMMAND_HISTORY_SIZE = 100;
  private final CommandHistory commandHistory =
      new CommandHistory(COMMAND_HISTORY_SIZE);

  public SerialMonitor(BoardPort port) {
    super(port);
    unbuffered=PreferencesData.getBoolean("serial.unbuffered",false);
    serialRate = PreferencesData.getInteger("serial.debug_rate");
    serialRates.setSelectedItem(serialRate + " " + tr("baud"));
    onSerialRateChange((ActionEvent event) -> {
      String wholeString = (String) serialRates.getSelectedItem();
      String rateString = wholeString.substring(0, wholeString.indexOf(' '));
      serialRate = Integer.parseInt(rateString);
      PreferencesData.set("serial.debug_rate", rateString);
      if (serial != null) {
        try {
          close();
          Thread.sleep(100); // Wait for serial port to properly close
          open();
        } catch (InterruptedException e) {
          // noop
        } catch (Exception e) {
          System.err.println(e);
        }
      }
    });

    onSerialPortChange((ActionEvent event) -> {
      if (serial != null) {
        try {
          close();
          Thread.sleep(100); // Wait for serial port to properly close
          open();
        } catch (InterruptedException e) {
          // noop
        } catch (Exception e) {
          System.err.println(e);
        }
      }
    });

    onSendCommand((ActionEvent event) -> {
      String command = textField.getText();
      if(!unbuffered) {//[980f] don't resend, but do pass into history mechanism.
        send(command);
      }
      commandHistory.addCommand(command);
      textField.setText("");
    });

    onClearCommand((ActionEvent event) -> textArea.setText(""));
    onConnectDisconnectCommand((ActionEvent event) -> {
      if(serial != null) {
        try {
          serial.dispose();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
        serial = null;
        connectionDisabled = true;
        enableWindow(false);
        ((JButton) event.getSource()).setText(tr("Connect"));
      } else {
        try {
          String serialPortAddr = getSerialPortAddr();
          serial = new Serial(serialPortAddr, serialRate) {
            @Override
            protected void message(char buff[], int n) {
              addToUpdateBuffer(buff, n);
            }
          };
          setTitle(serialPortAddr);
          connectionDisabled = false;
          enableWindow(true);
          ((JButton) event.getSource()).setText(tr("Disconnect"));
        } catch (SerialException e) {
          e.printStackTrace();
        }
      }
    });

    // Add key listener to UP, DOWN, ESC keys for command history traversal.
    textField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        String poo = textField.getText();
          if(unbuffered) {
//            send(poo);
            serial.write(poo);
            textField.setText("");
          }
        switch (e.getKeyCode()) {
          case KeyEvent.VK_ENTER:
            if(unbuffered) {
                send("");
            }
            break;
//          String command = textField.getText();
          // Select previous command.
          case KeyEvent.VK_UP:
            if (commandHistory.hasPreviousCommand()) {
              textField.setText(
                  commandHistory.getPreviousCommand(textField.getText()));
            }
            break;

          // Select next command.
          case KeyEvent.VK_DOWN:
            if (commandHistory.hasNextCommand()) {
              textField.setText(commandHistory.getNextCommand());
            }
            break;

          // Reset history location, restoring the last unexecuted command.
          case KeyEvent.VK_ESCAPE:
            textField.setText(commandHistory.resetHistoryLocation());
            break;
        }
      }
    });
  }

  private void send(String s) {
    if (serial != null) {
      switch (lineEndings.getSelectedIndex()) {
        case 1:
          s += "\n";
          break;
        case 2:
          s += "\r";
          break;
        case 3:
          s += "\r\n";
          break;
        default:
          break;
      }
      if ("".equals(s) && lineEndings.getSelectedIndex() == 0 && !PreferencesData.has("runtime.line.ending.alert.notified")) {
        noLineEndingAlert.setForeground(Color.RED);
        PreferencesData.set("runtime.line.ending.alert.notified", "true");
      }
      try {
        serial.write(s);
      } catch (NullPointerException e) {
        serial = null;
        connectDisconnectButton.setText(tr("Connect"));
        try {
          String serialPortAddr = getSerialPortAddr();
          serial = new Serial(serialPortAddr, serialRate) {
            @Override
            protected void message(char buff[], int n) {
              addToUpdateBuffer(buff, n);
            }
          };
          setTitle(serialPortAddr);
          connectDisconnectButton.setText(tr("Disconnect"));
          serial.write(s);
        } catch (SerialException e1) {
          throw new RuntimeException(e1);
        }
      }
    }
  }

  @Override
  public void enableWindow(boolean enable) {
    if(enable && connectionDisabled) {
      return;
    }
    super.enableWindow(enable);
  }

  @Override
  public void open() throws Exception {
    super.open();

    if (serial != null || connectionDisabled) return;

    String serialPortAddr = getSerialPortAddr();
    serial = new Serial(serialPortAddr, serialRate) {
      @Override
      protected void message(char buff[], int n) {
        addToUpdateBuffer(buff, n);
      }
    };
    setTitle(serialPortAddr);
    connectDisconnectButton.setText(tr("Disconnect"));
  }

  @Override
  public void close() throws Exception {
    super.close();
    if (serial != null) {
      int[] location = getPlacement();
      String locationStr = PApplet.join(PApplet.str(location), ",");
      PreferencesData.set("last.serial.location", locationStr);
      serial.dispose();
      serial = null;
      connectDisconnectButton.setText(tr("Connect"));
    }
  }

  private String getSerialPortAddr() {
    String portAddr = (String) serialPorts.getSelectedItem();
    if(portAddr == null) {
      portAddr = getBoardPort().getAddress();
    }
    return portAddr;
  }
  
}
