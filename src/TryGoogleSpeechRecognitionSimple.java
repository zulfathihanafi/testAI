import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;

import net.sourceforge.javaflacencoder.FLACFileWriter;

public class TryGoogleSpeechRecognitionSimple implements GSpeechResponseListener {
    static String forDialogue;

    public static void main(String[] args) throws IOException {
        final Microphone mic = new Microphone(FLACFileWriter.FLAC);
        //Don't use the below google api key , make your own !!! :)
        GSpeechDuplex duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");

        duplex.setLanguage("en");

        JFrame frame = new JFrame("Jarvis Speech API DEMO");
        frame.setDefaultCloseOperation(3);
        JTextArea response = new JTextArea();
        response.setFont(new Font("Arial", Font.BOLD, 20));
        response.setEditable(false);
        response.setWrapStyleWord(true);
        response.setLineWrap(true);

        final JButton record = new JButton("Record");
        final JButton stop = new JButton("Search");
        stop.setEnabled(false);

        record.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new Thread(() -> {
                    try {
                        duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }).start();
                record.setEnabled(false);
                stop.setEnabled(true);
            }
        });
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                mic.close();
                duplex.stopSpeechRecognition();
                record.setEnabled(true);
                stop.setEnabled(false);

                if (!forDialogue.equals("")) {
                    DialogueBox(forDialogue);
                }
            }
        });
        JLabel infoText = new JLabel(
                "<html><div style=\"text-align: center;\">Just hit record and watch your voice be translated into text.\n<br>Only English is supported by this demo, but the full API supports dozens of languages.<center></html>",

                0);
        frame.getContentPane().add(infoText);
        infoText.setAlignmentX(0.5F);
        JScrollPane scroll = new JScrollPane(response);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), 1));
        frame.getContentPane().add(scroll);
        JPanel recordBar = new JPanel();
        frame.getContentPane().add(recordBar);
        recordBar.setLayout(new BoxLayout(recordBar, 0));
        recordBar.add(record);
        recordBar.add(stop);
        frame.setVisible(true);
        frame.pack();
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        duplex.addResponseListener(new GSpeechResponseListener() {
            String old_text = "";

            public void onResponse(GoogleResponse gr) {
                String output = "";
                output = gr.getResponse();
                if (gr.getResponse() == null) {
                    this.old_text = response.getText();
                    if (this.old_text.contains("(")) {
                        this.old_text = this.old_text.substring(0, this.old_text.indexOf('('));
                    }
                    System.out.println("Paragraph Line Added");
                    this.old_text = (response.getText() + "\n");
                    this.old_text = this.old_text.replace(")", "").replace("( ", "");
                    response.setText(this.old_text);
                    return;
                }
                if (output.contains("(")) {
                    output = output.substring(0, output.indexOf('('));
                }
                if (!gr.getOtherPossibleResponses().isEmpty()) {
                    output = output + " (" + (String) gr.getOtherPossibleResponses().get(0) + ")";
                }
                System.out.println(output);
                response.setText("");
                response.append(this.old_text);
                response.append(output);
                forDialogue = output;
            }
        });
    }

    @Override
    public void onResponse(GoogleResponse paramGoogleResponse) {
        // TODO Auto-generated method stub

    }

    static void DialogueBox(String str) {
        JFrame f = new JFrame();
        String movieName = "";

        if (str.contains("football") && str.contains("kung fu")) {
            movieName = "Shaolin Soccer";
        }
        if (str.contains("corruption") && (str.contains("gambling") || str.contains("gamble"))) {
            movieName = "Kaiji";
        }
        if (str.contains("die") && str.contains("spring")) {
            movieName = "Your Lie in April 四月は君の噓";
        }

        if (!movieName.equals("")) {
            JOptionPane.showMessageDialog(f, "Your movie is " + movieName, "Your movie is here", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(f, "Sorry, we can't find your movie in our database", "Sorry", JOptionPane.ERROR_MESSAGE);
        }


    }
}
