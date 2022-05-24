package com.example.ai_mad_numgame;
/*
   App will show your last performance at the start of the activity. New Tournament will start from
   all performance set to -1 again. And your new performance will be visible, when you return back to game
 */
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;  //make changes at appropriate places to include this dependency

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    int matchCounter=0;
    int []performance={-1,-1,-1,-1,-1,-1}; //score of a game is updated in this array
    int []score={-1,-1,-1}; //score of each match is updated in this array. A total of three matches in a game
    String operators[]={"+","-","*","/"};
    int correctButton= 0; //which button will have the correct answer (tag of that button)
    Random random=new Random(); //You will generate random algebra questions
    TextView textView2;
    Button button1,button2,button3,button4;
    public void load(View view){
        Button buttonClicked=(Button)view;
        if(buttonClicked.getTag().toString().equals(correctButton+"")){
            score[matchCounter++]=1;
        }else{
            score[matchCounter++]=0;
        }
        newMatch();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);
        textView2=findViewById(R.id.textView2);
        newMatch();
        sharedPreferences=this.getSharedPreferences("com.example.aiapp_2022", Context.MODE_PRIVATE);
        int[][]dataFrame=dataPrep(); //dataPrep function returns a two-dimenssional array
        double slope=LR.getSlope(dataFrame); //LR class, which provides slope on invoking getSlope
        new AlertDialog.Builder(this)
                // .setIcon() //your custom icon
                .setTitle("Performance")

                .setMessage(getInterpretation(dataFrame,slope))
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newMatch();
                    }
                }).show();
    }

    public void newMatch() {  //A game is composed of three matches


        int operand1 = random.nextInt(10);
        int operand2 = random.nextInt(10);
        correctButton = random.nextInt(4);
        String operator = operators[random.nextInt(4)];
        textView2.setText(operand1 + operator + operand2);

        int correct_answer = -100;
        if(operator.equals("+"))
            correct_answer = operand1 + operand2;
        else if(operator.equals("-"))
            correct_answer = operand1 - operand2;
        else if(operator.equals("*"))
            correct_answer = operand1 * operand2;
        else
            correct_answer = operand1 / operand2;

        if(correctButton == 0)
        {
            button1.setText(correct_answer + "");
            button2.setText(correct_answer + 1 + "");
            button3.setText(correct_answer - 1  + "");
            button4.setText(correct_answer + 2 +"");
        }
        else if(correctButton == 1)
        {
            button1.setText(correct_answer + 1 + "");
            button2.setText(correct_answer + "");
            button3.setText(correct_answer - 1  + "");
            button4.setText(correct_answer + 2 +"");
        }
        else if(correctButton == 2)
        {
            button1.setText(correct_answer + 1 + "");
            button2.setText(correct_answer - 1 + "");
            button3.setText(correct_answer + "");
            button4.setText(correct_answer + 2 +"");
        }
        else
        {
            button1.setText(correct_answer + 1 + "");
            button2.setText(correct_answer - 1 + "");
            button3.setText(correct_answer + 2 + "");
            button4.setText(correct_answer + "");
        }
        //check is operand2 is not zero; otherwise in case of division-divide by zero error will come


        // Your code here, to display correct and incorrect options on the buttons

        if(matchCounter==3){    // if three matches are completed update the performance in sharedpreferences

            matchCounter=0;

            for(int i=0;i<performance.length-1;i++){ //adjusting the performance array so that last six entries present with the most recent at the last index.
                performance[i]=performance[i+1];
            }
            performance[5]=sumOfScore(); //calculating the sum of last three matches (note result of a match is 1 ro 0, and add to performance
            sharedPreferences.edit().putString("data",new Gson().toJson(performance)).apply();

        }
    }

    public int sumOfScore(){
        //Computing the sum of score array, which has the 1 or in each index,depending on correct or incorrect answers
        int sum=0;

        for(int i = 0; i < score.length; i++)
        {
            sum += score[i];
        }
        // your code here
        return sum;
    }

    public int[][] dataPrep() {
        int[] data = new Gson().fromJson((sharedPreferences.getString("data", null)), performance.getClass());
        Log.i("data", Arrays.toString(data)); //this is how you display arrays in Logcat, for debugging
        int dataFrame[][] = new int[6][2]; //creating a dataframe of two columns and six rows for regresson purpose
        if(data==null)
            return null;
        for (int i = 0; i < data.length; i++) {
            dataFrame[i][0] = i + 1;
            dataFrame[i][1] = data[i];
        }
        return dataFrame;
    }

    public String getInterpretation(int [][]dataFrame,double slope){
        //provide interpretation based on your slope analysis
       double myslope= LR.getSlope(dataFrame);
       if(myslope>0 && myslope<0.5)
           return "You are slow but steady";
       else if(myslope<0 )
           return "The slope is negative you are not serious";
       else
           return "the slope is positive";
    }
}
