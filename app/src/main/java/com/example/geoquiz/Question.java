package com.example.geoquiz;

public class Question {

    private int mTextResId;
    private boolean mAnswerTrue;
    private int mUserAnswer;

    public Question (int textResId, boolean answerTrue){

        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mUserAnswer = 0;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public int getUserAnswer() {
        return mUserAnswer;
    }

    public void setUserAnswer(int userAnswer) {
        mUserAnswer = userAnswer;
        /*User answer was: 1=Correct 2=Incorrect*/
    }
}
