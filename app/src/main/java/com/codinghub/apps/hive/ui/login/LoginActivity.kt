package com.codinghub.apps.hive.ui.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codinghub.apps.hive.BuildConfig
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.login.CurrentUser
import com.codinghub.apps.hive.model.login.LoginType
import com.codinghub.apps.hive.model.login.LoginResponse
import com.codinghub.apps.hive.model.login.UserRole
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.ui.main.MainActivity
import com.codinghub.apps.hive.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    private val TAG = LoginActivity::class.qualifiedName

    private var userName: String = ""
    private var password: String = ""
    private var schoolID: String = ""
//    private var isChooseLoginType: Boolean = false
//    private var loginType: LoginType = LoginType.PARENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        checkLoginState()

        updateUI()

        schoolIDTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                schoolID = schoolIDTextView.text.toString()
                updateUI()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        userNameTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userName = userNameTextView.text.toString()
                updateUI()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        passwordTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                password = passwordTextView.text.toString()
                updateUI()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

//        loginTypeChipGroup.setOnCheckedChangeListener { group, checkedId ->
//            for (i in 0 until group.childCount) {
//                val chip = group.getChildAt(i)
//                chip.isClickable = chip.id != group.checkedChipId
//            }
//
//            when (checkedId) {
//                R.id.chipParent -> {
//                    isChooseLoginType = true
//                    loginType = LoginType.PARENT
//                }
//                R.id.chipTeacher -> {
//                    isChooseLoginType = true
//                    loginType = LoginType.TEACHER
//                }
//                else -> {
//                    isChooseLoginType = false
//                }
//            }
//            updateUI()
//        }

        loginBotton.setOnClickListener {
            login(userName, password, schoolID)
        }

        constraintLayout.setOnTouchListener { view, _ ->
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        appVersionTextView.text = "${getString(R.string.app_version, BuildConfig.VERSION_NAME)} - Build ${BuildConfig.VERSION_CODE}"

    }

    private fun  updateUI() {
        loginBotton.isEnabled = userNameTextView.text!!.isNotEmpty() && passwordTextView.text!!.isNotEmpty() && schoolIDTextView.text!!.isNotEmpty()

    }

    private fun login(username: String, password: String, schoolID: String) {

        loginViewModel.schoolLogin(username, password, schoolID).observe(this, Observer<Either<LoginResponse>> { either ->
            if (either?.status == Status.SUCCESS && either.data != null) {
                if (either.data.ret == 0) {
                    Log.d(TAG, "Login Successfull")

                    val role = either.data.role

                    when {
                        role.contains("ADMIN") -> {
                            Log.d(TAG, "ADMIN to save")
                            saveLoginState(CurrentUser(username, UserRole.ADMIN))
                            saveSchoolID(schoolID)
                            loginViewModel.registerUserForPushNotification(schoolID, userName, UserRole.ADMIN.rolename)
                            pushToMainActivity()
                        }
                        role.contains("TEACHER") -> {
                            Log.d(TAG, "TEACHER to save")
                            saveLoginState(CurrentUser(username, UserRole.TEACHER))
                            saveSchoolID(schoolID)
                            loginViewModel.registerUserForPushNotification(schoolID, userName, UserRole.TEACHER.rolename)
                            pushToMainActivity()
                        }
                        role.contains("PARENT") -> {
                            Log.d(TAG, "PARENT to save")
                            saveLoginState(CurrentUser(username, UserRole.PARENT))
                            saveSchoolID(schoolID)
                            loginViewModel.registerUserForPushNotification(schoolID, userName, UserRole.PARENT.rolename)
                            pushToMainActivity()

                        }
                        else -> {
                            Toast.makeText(this, R.string.incorrect_user_id, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                else {
                    Toast.makeText(this, R.string.incorrect_user_id, Toast.LENGTH_SHORT).show()
                }
            } else {
                if (either?.error == ApiError.SCHOOL_LOGIN) {
                    Toast.makeText(this, R.string.error_retrieving_information, Toast.LENGTH_SHORT).show()
                }
            }
        })


//        loginViewModel.getParents().observe(this, Observer<Either<ParentResponse>> { either ->
//            if (either?.status == Status.SUCCESS && either.data != null) {
//                if (either.data.ret == 0) {
//                    Log.d(TAG, "StudentData : ${either.data.parents}")
//
//                    for (parent in either.data.parents) {
//                        if (userID == parent.tel) {
//                            saveLoginState(CurrentUser(parent.parent_pid, LoginType.PARENT))
//                            pushToMainActivity()
//                        } else {
//                            Toast.makeText(this, R.string.incorrect_user_id, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                } else {
//
//                    Toast.makeText(this, R.string.error_retrieving_information, Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                if (either?.error == ApiError.STUDENTS) {
//
//                    Toast.makeText(this,R.string.error_retrieving_information, Toast.LENGTH_SHORT).show()
//                }
//            }
//        })

    }

    private fun checkLoginState() {
        if (loginViewModel.getLoginStatus()) {
            pushToMainActivity()
        }
    }

    private fun saveLoginState(currentUser: CurrentUser) {
        loginViewModel.saveCurrentUser(currentUser)
        loginViewModel.saveLoginStatus(true)
    }

    private fun saveSchoolID(schoolID: String) {
        loginViewModel.saveSchoolID(schoolID)
    }

    private fun pushToMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }

}
