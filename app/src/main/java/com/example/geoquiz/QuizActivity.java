package com.example.geoquiz;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEATER = "cheater";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATER,false);
        }

        /*Challenge (6) display API Level */
        TextView Level = (TextView) findViewById(R.id.api_level);
        Level.setText("API Level " + Build.VERSION.SDK_INT);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        /* Challenge (2) when I click on the text view it passes to next question*/
        /*
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex+1) % mQuestionBank.length;
                updateQuestion();
            }
        });*/

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestionBank[mCurrentIndex].getUserAnswer()==0) {
                    checkAnswer(true);
                    mTrueButton.setEnabled(false);
                    mFalseButton.setEnabled(false);
                }
                verifyCompleteQuiz();
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestionBank[mCurrentIndex].getUserAnswer()==0) {
                    checkAnswer(false);
                    mTrueButton.setEnabled(false);
                    mFalseButton.setEnabled(false);
                }
                verifyCompleteQuiz();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex+1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
                if (mQuestionBank[mCurrentIndex].getUserAnswer()==0) {
                    mTrueButton.setEnabled(true);
                    mFalseButton.setEnabled(true);
                }else{
                    mTrueButton.setEnabled(false);
                    mFalseButton.setEnabled(false);
                }
            }
        });

        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = mCurrentIndex-1;
                if(mCurrentIndex<0) mCurrentIndex = mQuestionBank.length-1;
                mIsCheater = false;
                updateQuestion();
                if (mQuestionBank[mCurrentIndex].getUserAnswer()==0) {
                    mTrueButton.setEnabled(true);
                    mFalseButton.setEnabled(true);
                }else{
                    mTrueButton.setEnabled(false);
                    mFalseButton.setEnabled(false);
                }
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this,answerIsTrue);
                startActivityForResult(intent,REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    /*It's not working*/
    private void verifyCompleteQuiz(){
        double countCorrect = 0;
        double questionsMade = 0;
        for (Question q : mQuestionBank) {
            if(q.getUserAnswer()==1) countCorrect = countCorrect+1;
            if(q.getUserAnswer()!=0) questionsMade = questionsMade+1;
        }

        if(mQuestionBank.length == questionsMade) {
            double score = countCorrect/questionsMade;
            Toast.makeText(this, String.format("Quiz finished: %.2f correct", score), Toast.LENGTH_LONG).show();
        }
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (mIsCheater==true){
            messageResId = R.string.judgment_toast;
            if (userPressedTrue == answerIsTrue) {
                mQuestionBank[mCurrentIndex].setUserAnswer(1);
            } else {
                mQuestionBank[mCurrentIndex].setUserAnswer(2);
            }
        }else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mQuestionBank[mCurrentIndex].setUserAnswer(1);

            } else {
                messageResId = R.string.incorrect_toast;
                mQuestionBank[mCurrentIndex].setUserAnswer(2);
            }
        }

        Toast toastAnswer = Toast.makeText(this,messageResId,Toast.LENGTH_SHORT);
        toastAnswer.setGravity(Gravity.TOP,0,200);
        toastAnswer.show();
    }

    /*Overrides onCreate(Bundle)*/
    @Override
    public void  onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_CHEATER, mIsCheater);

    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"OnStart() called");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }


}