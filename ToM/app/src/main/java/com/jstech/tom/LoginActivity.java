package com.jstech.tom;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    static final int RET_LOGIN_FAIL = 0;
    static final int RET_LOGIN_SUCCESS = 1;
    static final int RET_LOGIN_CHOICE = 2;

    String mStrUrl;
    String mParameter;
    EditText mEditText_Email;
    EditText mEditText_Password;
    TextView mTextView_Login;
    TextView mTextView_SignUp;
    TextView mTextView_FindAccount;

    int mIntRet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitResource();
    }

    /*
    *
    *   각 리소스 객체에 할당.
    *   mEditText_Email - 사용자 입력 이메일 주소
    *   mEditText_Password - 사용자 입력 비밀번호
    *   mTextView_Login - 로그인 클릭
    *   mTextView_SignUp - 회원가입 클릭
    *   mTextView_Find - 아이디 / 비밀번호 찾기
    *
    * */
    public void InitResource()
    {
        mEditText_Email = (EditText)findViewById(R.id.login_et_email);
        mEditText_Password = (EditText)findViewById(R.id.login_et_password);
        mTextView_Login = (TextView)findViewById(R.id.login_tv_login);
        mTextView_Login.setOnClickListener(this);
        mTextView_SignUp = (TextView)findViewById(R.id.login_tv_signup);
        mTextView_SignUp.setOnClickListener(this);
        mTextView_FindAccount = (TextView)findViewById(R.id.login_tv_find_account);
        mTextView_FindAccount.setOnClickListener(this);

        mStrUrl = getResources().getString(R.string.url_login);

        mIntRet = RET_LOGIN_FAIL;
    }

    /*
    *
    *   클릭 이벤트 처리
    *   login_tv_login - 로그인 이벤트
    *   login_tv_signup - 회원가입 이벤트
    *   login_tv_find - 아이디 / 비밀번호 찾기 이벤트
    *
    * */
    @Override
    public void onClick(View v) {

        Intent intent;
        switch(v.getId()){
            case R.id.login_tv_login:
                DoLogin();
                break;
            case R.id.login_tv_signup:
                intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.login_tv_find_account:
                intent = new Intent(this,FindAccountActivity.class);
                startActivity(intent);
                break;
        }
    }

    /*
    *
    *   로그인 이벤트 처리
    *   아이디와 패스워드를 서버로 전송해 Valid Check한다.
    *   정상적이면 메인페이지로, 아니면 토스트 메시지.
    *
    * */
    public void DoLogin()
    {
        String strEmail = mEditText_Email.getText().toString();
        String strPassword = mEditText_Password.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("id", strEmail);
            jsonObject.accumulate("password", strPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String parameter = jsonObject.toString();

        // JSON 포맷으로 변경해야함.
        mParameter = "id=" + strEmail + "&password=" + strPassword;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkTask networkTask = new NetworkTask(mStrUrl, mParameter);
                networkTask.execute();
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /*
    *
    *   이메일과 비밀번호를 서버로 전송한 뒤, 그에 따른 결과에 따라 처리.
    *   RET_LOGIN_FAIL - 로그인 실패
    *   RET_LOGIN_SUCCESS - 로그인 성공 및 기존 이용중인 방이 있는 경우
    *   RET_LOGIN_CHOICE - 로그인 성공이나 기존에 이용중인 방이 없는 경우
    *
    * */
    public void DoLoginActionForResult(int iHttpRet)
    {

        Log.e("Ret", Integer.toString(iHttpRet));

        Intent intent;
        if(iHttpRet == RET_LOGIN_FAIL)
        {
            Toast.makeText(this, getResources().getString(R.string.toast_login_fail), Toast.LENGTH_SHORT).show();
        }
        else if(iHttpRet == RET_LOGIN_SUCCESS)
        {
            Toast.makeText(this, getResources().getString(R.string.toast_login_success), Toast.LENGTH_SHORT).show();
        }
        else if(iHttpRet == RET_LOGIN_CHOICE)
        {
            Toast.makeText(this, getResources().getString(R.string.toast_login_choice), Toast.LENGTH_SHORT).show();
            intent = new Intent(this, StartChoiceActivity.class);
            startActivity(intent);
        }

    }

    public class NetworkTask extends AsyncTask<Void, Void, String>
    {

        private String strUrl;
        private String strParam;

        //  URL과 파라미터 초기화
        public NetworkTask(String url, String values) {

            this.strUrl = url;
            this.strParam = values;
        }

        //  URL과 파라미터로 post 요청 후, 결과 리턴
        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpUrlConnection requestHttpURLConnection = new RequestHttpUrlConnection();
            result = requestHttpURLConnection.request(strUrl, strParam); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        /*
        *
        *   서버로부터 받은 데이터 처리 및 동작 지정
        *
        * */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null)
            {
                Log.e("Result Code", s);
            }

            int iPosition = s.indexOf("<body>");
            if(iPosition == -1)
            {
                return;
            }

            char cRet = s.charAt(iPosition + 6);
            Log.e("Result Code", Character.toString(cRet));


            int iRet = RET_LOGIN_FAIL;
            if(cRet == '0')
            {
                iRet  = RET_LOGIN_FAIL;
            }
            else if(cRet == '1')
            {
                iRet = RET_LOGIN_SUCCESS;
            }
            else if(cRet == '2')
            {
                iRet = RET_LOGIN_CHOICE;
            }

            DoLoginActionForResult(iRet);

        }
    }

}
