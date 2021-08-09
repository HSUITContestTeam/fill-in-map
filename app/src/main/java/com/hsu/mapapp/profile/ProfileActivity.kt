package com.hsu.mapapp.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.ActivityProfileBinding
import com.hsu.mapapp.login.LoginActivity


class ProfileActivity : AppCompatActivity() {
    private lateinit var profileBinding : ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        setProfileModifyBtnClickEvent()
        setUpdatePasswordBtn()
        profileBinding.logoutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("정말로 로그아웃 하시겠습니까?")

            builder.setPositiveButton("예") {dialog, which ->
                signOut()
            }
            builder.setNegativeButton("아니오") {dialog, which ->
                builder.setCancelable(true)
            }
            builder.show()
        }

        profileBinding.deleteAccountBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("정말로 탈퇴 하시겠습니까?")

            builder.setPositiveButton("예") {dialog, which ->
                revokeAccess()
            }
            builder.setNegativeButton("아니오") {dialog, which ->
                builder.setCancelable(true)
            }
            builder.show()

        }

        //---------------------사용자 정보 load----------------------//

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()

        // 구글 로그인 정보
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        //----------------------------------------------------------//

        setProfile()

    }

    private fun setProfile() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            user?.let {
                val name = user.displayName
                val email = user.email
                val photoUrl = user.photoUrl
                val emailVerified = user.isEmailVerified
                val uid = user.uid

                profileBinding.profileNameTV.text = name
                profileBinding.profileEmailTV.text = email
            }
        } else {
            // No user is signed in
        }
    }

    private fun setProfileModifyBtnClickEvent() { // 프로필 수정 버튼 이벤트
        profileBinding.profileModifyBtn.setOnClickListener {
            startActivity(Intent(this, CropImg::class.java))
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        }
    }

    private fun signOut() { // 로그아웃
        firebaseAuth.signOut() // Firebase sign out
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
        }

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    private fun revokeAccess() { //회원탈퇴
        val user = Firebase.auth.currentUser!!
        signOut()
        user.delete()
            .addOnCompleteListener { task ->
                Toast.makeText(this, "회원 정보 삭제 완료", Toast.LENGTH_SHORT).show()
            }

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    //---------------------비밀번호 재설정----------------------//
    private fun setUpdatePasswordBtn() { // 비밀번호 재설정 버튼 이벤트
        profileBinding.passwordChangeBtn.setOnClickListener {
            startActivity(Intent(this, UpdatePasswordActivity::class.java))
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        }
    }

}