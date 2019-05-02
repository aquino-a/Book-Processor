package com.aquino.webParser;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class OclcProgress {
    private JDialog dialog;
    private JProgressBar progressBar;
    private Frame frame;
    private JLabel labelEstimatedTime;
    private long startTime;
    private int estimatedMillisecondsRemaining;
    private Timer timer;

    public OclcProgress(Frame frame) {
        this.frame = frame;
    }

    public void start(){
        if(timer == null)
            timer = new Timer(1000, e -> { labelEstimatedTime.setText(timeLeftToString(estimatedMillisecondsRemaining));estimatedMillisecondsRemaining -= 1000; });
        startTime = System.currentTimeMillis();
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        display();
    }

    private String timeLeftToString(int milliseconds) {
        int seconds = (milliseconds/1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours % 60, minutes % 60, seconds % 60);
    }

    private void display() {
        if(dialog == null)
            SetupDialog();
        dialog.setVisible(true);
    }

    private void SetupDialog() {
        dialog = new JDialog(frame,"Oclc Scrape Progress", false);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setSize(200,100);
//        dialog.setUndecorated(true);
        dialog.setLayout(new FlowLayout(FlowLayout.CENTER));
        dialog.add(labelEstimatedTime = new JLabel("Estimating..."));
        dialog.add(progressBar = new JProgressBar(0,100));
        dialog.setResizable(false);

    }

    public void setProgress(ProgressData data){
        if(data.getCurrent() == data.getEnd()){
            reset();
            return;
        }
        estimatedMillisecondsRemaining = findRemainingTime(startTime, System.currentTimeMillis(), data);
        progressBar.setValue(calculateValue(data));
        if(timer != null && !timer.isRunning())
            timer.start();
    }

    private int calculateValue(ProgressData data) {
        int total = data.getEnd() + 1 - data.getStart();
        return (((data.getCurrent()+1 - data.getStart())*100)/total);
    }

    private int findRemainingTime(long startTime, long currentTimeMillis, ProgressData data) {
        long elapsedTime = currentTimeMillis - startTime;
        int done = (data.getCurrent() + 1) - data.getStart();
        int left = data.getEnd() - data.getCurrent();
        double timePerProgress = elapsedTime / done;
        return (int)(timePerProgress * left);
    }

    private void reset(){
        frame.setCursor(null);
        dialog.dispose();
        dialog = null;
        timer.stop();
    }

    public static void main(String[] args) {
            JFrame frame = new JFrame();
            frame.setSize(500, 150);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            OclcProgress p = new OclcProgress(frame);
            try{
                p.start();
            }
            catch (Exception e){
                e.printStackTrace();
            }

            for (int i = 0; i <= 30; i++) {
                try {
                    Thread.sleep(1000);
                    p.setProgress(new ProgressData(0,i,30));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

    }
}
