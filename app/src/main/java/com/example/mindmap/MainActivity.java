package com.example.mindmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;


    // 요 부분에서 로그인후 넘어갈 Activity 지정하면 됨
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            UserInfo.getInstance().setUserId(user);
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
            finish();
        }
    }


    public void googleLogin(){
        signInButton = findViewById(R.id.signInButton);

        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        googleLogin();
        //Intent intent = new Intent(this, ListActivity.class);
        //Intent intent = new Intent(this, MindMapEditorActivity.class);
        //startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Snackbar.make(findViewById(R.id.layout_main), "Authentication Successed.", Snackbar.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.layout_main), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}

class TestGetMindMap{
    SaveNodeFirebase db;
    TestGetMindMap(){
        db = new SaveNodeFirebase();
    }

    // 처음 마인드맵 새로 만들때 쓰는 거
    public void createNewMindMap(String startWord, String explain){
        String id = db.createNewMindMapId(); // 새롭게 id 만들고
        db.setCurrentId(id);
        db.writeNodes(new Node(null, startWord)); // 노드 생성하고 데이터베이스 보내고
        db.writeMindMapExplain(explain); // 설명 데이터베이스에 보내고
        db.writeMindMapExplain(null); // 이미지 임시로 null 값 보내고
        // id 는 createNewMindMapId() 넣으면 자동 동기화
    }

    // 처음 메인메뉴 진입할때 firebase에 저장된 자기꺼  MindMap 모두 불러오는거
    public void loadAllMindMap(){
        db.readAllMindMapInfo(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<db.mindMapdataList.size();i++){
                    MindMapData mindMapData = db.mindMapdataList.get(i);

                    // 이거 3개값 알아서 따로 저장하면됨
                    Log.d("id 값", mindMapData.getId());
                    Log.d("image string 값",mindMapData.getId());
                    Log.d("설명", mindMapData.getExplain());
                }
            }
        });
    }

    // 메인메뉴에서 MindMap 리스트 보여준후 거기서 선택해서 그 선택한 값 id값을 인자로 넘기면됨
    public void selectMindMap(String mindMapId){
        db.setCurrentId(mindMapId);
        db.loadNodes(new Runnable() {
            @Override
            public void run() {
                String nodeJsonString = db.getPost();
                // nodeJsonString 값을 MinMapEditorActiviy 에 넘겨주고 거기서  밑에 껄 실행하면 될듯?
                Node rootNode = db.CreateNode(nodeJsonString, null);
            }
        });
    }
}