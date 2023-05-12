package com.godsaeng.godsaengfinal.ui.utilities;

import android.app.Activity;
import android.util.Patterns;
import android.widget.EditText;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class FBAuth {

    private static FBAuth instance;

    @MainThread
    public static FBAuth getInstance() {
        if (instance == null) instance = new FBAuth();
        return instance;
    }

    private final FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
    private String signedUpEmail;
    private String signedUpPassword;

    public FirebaseUser getUser() {
        return firebaseAuth.getCurrentUser();
    }

    private String getText(EditText editText)  {
        return editText.getText().toString().trim();
    }

    public void signInWithEmail(EditText editEmail, EditText editPW, AuthResult listener) {
        String email = getText(editEmail);
        String password = getText(editPW);
        String msg = verifyEmailAndPW(email, password);
        if (msg != null) {
            listener.onFail(msg);
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                if(task.isSuccessful()){
                    listener.onSuccess();
                } else {
                    String msg = "오류가 발생하였습니다.";
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        //The password is invalid or the user does not have a password.
                        msg = "비밀번호가 일치하지 않습니다.";
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        //There is no user record corresponding to this identifier. The user may have been deleted.
                        msg = "이메일 주소를 확인하세요.";
                    }
                    listener.onFail(msg);
                }
            }
        });
    }

    public void signUpWithEmail(Activity activity, EditText editEmail, EditText editPW, EditText editPWConfirm, AuthResult listener) {
        String email = getText(editEmail);
        String password = getText(editPW);
        String passwordConfirm = getText(editPWConfirm);

        String msg;
        if (!password.equals(passwordConfirm)) msg = "비밀번호가 일치하지 않습니다.";
        msg = verifyEmailAndPW(email, password);
        if (msg != null) {
            listener.onFail(msg);
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> result) {
                if(result.isSuccessful()){
                    signedUpEmail = email;
                    signedUpPassword = password;
                    listener.onSuccess();
                } else {
                    String msg = "오류가 발생하였습니다.";
                    if (result.getException() instanceof FirebaseAuthUserCollisionException) {
                        //The email address is already in use by another account.
                        msg = "중복된 이메일 주소입니다. 다른 이메일을 입력하세요.";
                    } else if (result.getException() instanceof FirebaseAuthWeakPasswordException) {
                        //given password is invalid. [ Password should be at least 6 characters ]
                        msg = "잘못된 비밀번호입니다.";
                    }
                    listener.onFail(msg);
                }
            }
        });
    }

    private String verifyEmailAndPW(String email, String password) {
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "잘못된 이메일입니다.";
        }
        if(password.length() < 6) {
            return "비밀번호는 6자 이상입니다.";
        }
        return null;
    }

    public interface AuthResult {
        void onSuccess();
        void onFail(String msg);
    }

}

