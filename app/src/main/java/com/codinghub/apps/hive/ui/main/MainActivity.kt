package com.codinghub.apps.hive.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.login.CurrentUser
import com.codinghub.apps.hive.model.login.UserRole
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.grade.GradeData
import com.codinghub.apps.hive.model.student.grade.GradeResponse
import com.codinghub.apps.hive.ui.face.FaceFragment
import com.codinghub.apps.hive.ui.home.HomeFragment
import com.codinghub.apps.hive.ui.facerecognition.FaceRecognitionActivity
import com.codinghub.apps.hive.ui.login.LoginActivity
import com.codinghub.apps.hive.ui.myaccount.MyAccountFragment
import com.codinghub.apps.hive.ui.newmember.NewMemberActivity
import com.codinghub.apps.hive.ui.notifications.HiveNotificationOpenHandler
import com.codinghub.apps.hive.ui.notifications.NotificationsFragment
import com.codinghub.apps.hive.ui.settings.SettingsActivity
import com.codinghub.apps.hive.ui.students.grade.GradeFragment
import com.codinghub.apps.hive.ui.students.legacy.StudentsFragment
import com.codinghub.apps.hive.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var mainViewModel: MainViewModel

    private val TAG = MainActivity::class.qualifiedName

    private val homeFragment = HomeFragment()
    private val faceFragment = FaceFragment()
    private val studentFragment = StudentsFragment()
    private val myAccountFragment = MyAccountFragment()
    private val notificationsFragment = NotificationsFragment()

    private val gradeFragment = GradeFragment()

    private var isStudentFragment: Boolean = false

    companion object {
        const val INTENT_GRADE_KEY = "gradekey"
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        Log.d(TAG, "onNavigationItemSelectedListener")

        val fragment = when (item.itemId) {
            R.id.navigation_home -> homeFragment
            R.id.navigation_students -> studentFragment
            R.id.navigation_my_account -> myAccountFragment
            R.id.navigation_notifications -> notificationsFragment
            else -> HomeFragment()
        }
        switchToFragment(fragment)
        
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        nav_view_bottom.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


       // OneSignal.sendTag("role","${AppPrefs.getSchoolID()}:${mainViewModel.getCurrentUser().first().role.name}")

       // OneSignal.setExternalUserId("${AppPrefs.getSchoolID().toString()}:${mainViewModel.getCurrentUser().first().userId}")
       // OneSignal.setSubscription(true)


        when(mainViewModel.getCurrentUser().first().role) {
            UserRole.ADMIN -> {
                switchToFragment(homeFragment)
            }
            UserRole.TEACHER -> {
                switchToFragment(homeFragment)
            }
            UserRole.PARENT -> {
                nav_view.menu.findItem(R.id.nav_face_recognition).isVisible = false
                switchToFragment(myAccountFragment)
            }
        }

        when(intent.getStringExtra(HiveNotificationOpenHandler.INTENT_OPEN_NOTIFICATION_KEY)) {
            "REQUEST" -> {
                Log.d(TAG, "Should go to request")
                switchToFragment(notificationsFragment)
                AppPrefs.saveNotificationFlag("REQUEST")
                nav_view_bottom.menu.findItem(R.id.navigation_notifications).isChecked = true
            }
            "PICKUP" -> {
                Log.d(TAG, "Should go to Message")
                switchToFragment(notificationsFragment)
                AppPrefs.saveNotificationFlag("PICKUP")
                nav_view_bottom.menu.findItem(R.id.navigation_notifications).isChecked = true

            } else -> {

            }
        }

        configureUI(mainViewModel.getCurrentUser().first())

    }


    private fun switchToFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, fragment).commit()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        return false
    }

//        when(mainViewModel.getCurrentUser().first().role) {
//            UserRole.ADMIN -> {
//
//                menuInflater.inflate(R.menu.action_bar_spinner_menu, menu)
//                val item = menu.findItem(R.id.menu_spinner)
//                val spinner = item.actionView as Spinner
//
//                mainViewModel.listGrade(AppPrefs.getSchoolID().toString()).observe(this, Observer<Either<GradeResponse>> { either ->
//                    if (either?.status == Status.SUCCESS && either.data != null) {
//                        if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
//
//                            val grades: List<GradeData> = either.data.data
//                            val spinnerTitle = arrayListOf<String>()
//
//                            for (gradeName in grades) {
//                                spinnerTitle.add(gradeName.name)
//                            }
//
//                            spinner.adapter = ArrayAdapter<String>(this, R.layout.spinner_item, spinnerTitle)
//
//                            spinner.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
//                                override fun onNothingSelected(parent: AdapterView<*>?) {
//                                    Log.d(TAG, "On Nothing Selected")
//                                }
//                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                                    Log.d(TAG, "On Item Selected ${grades[position]}")
//
//                                    mainViewModel.saveSelectedGrade(grades[position])
//
//                                    if (isStudentFragment) {
//                                        studentFragment.reloadFragment()
//                                    }
//                                }
//                            }
//
//                        } else {
//
//                            Toast.makeText(this, "Student List is Empty", Toast.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        if (either?.error == ApiError.LIST_GRADE) {
//                            Toast.makeText(this, R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                })
//
//                return true
//            }
//
//            UserRole.TEACHER -> {
//
//                menuInflater.inflate(R.menu.action_bar_spinner_menu, menu)
//                val item = menu.findItem(R.id.menu_spinner)
//                val spinner = item.actionView as Spinner
//
//                mainViewModel.listGrade(AppPrefs.getSchoolID().toString()).observe(this, Observer<Either<GradeResponse>> { either ->
//                    if (either?.status == Status.SUCCESS && either.data != null) {
//                        if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
//
//                            val grades: List<GradeData> = either.data.data
//                            val spinnerTitle = arrayListOf<String>()
//
//                            for (gradeName in grades) {
//                                spinnerTitle.add(gradeName.name)
//                            }
//
//                            spinner.adapter = ArrayAdapter<String>(this, R.layout.spinner_item, spinnerTitle)
//
//                            spinner.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
//                                override fun onNothingSelected(parent: AdapterView<*>?) {
//                                    Log.d(TAG, "On Nothing Selected")
//                                }
//                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                                    Log.d(TAG, "On Item Selected ${grades[position]}")
//
//                                    mainViewModel.saveSelectedGrade(grades[position])
//                                    if (isStudentFragment) {
//                                        studentFragment.reloadFragment()
//                                    }
//                                }
//                            }
//
//                        } else {
//
//                            Toast.makeText(this, "Student List is Empty", Toast.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        if (either?.error == ApiError.LIST_GRADE) {
//                            Toast.makeText(this, R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                })
//
//                return true
//            }
//
//            UserRole.PARENT -> {
//                return false
//            }
//        }
  //  }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_adds -> {
//                showMenu()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_face_recognition -> {
                showKioskActivity()
            }
//            R.id.nav_how_to -> {
//
//            }
            R.id.nav_settings -> {
                showSettingsActivity()
            }
//            R.id.nav_tos -> {
//
//            }
//            R.id.nav_about -> {
//
//            }
            R.id.nav_logout -> {
                //OneSignal.deleteTag("ROLE")
                logout()
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout(){

        mainViewModel.unregisterUserForPushNotification()
        mainViewModel.removeCurrentUser()
        mainViewModel.saveLoginStatus(false)
        mainViewModel.removeSchoolID()
        mainViewModel.removeSelectedGrade()

        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(loginIntent)
    }

    private fun showMenu() {
        Log.d("LOG", "Show menu")
        showBottomDialog()
    }

    private fun configureUI(currentUser: CurrentUser) {

        nav_view.getHeaderView(0).headerTitleTextView.text = currentUser.userId
        nav_view.getHeaderView(0).headerSubTitleTextView.text = currentUser.role.name

        when (currentUser.role) {
            UserRole.ADMIN -> {

                nav_view_bottom.menu.removeItem(R.id.navigation_face)
                nav_view_bottom.menu.removeItem(R.id.navigation_my_account)
            }
            UserRole.TEACHER -> {

                nav_view_bottom.menu.removeItem(R.id.navigation_face)
                nav_view_bottom.menu.removeItem(R.id.navigation_my_account)
            }
            UserRole.PARENT -> {
                nav_view_bottom.menu.removeItem(R.id.navigation_home)
                nav_view_bottom.menu.removeItem(R.id.navigation_face)
                nav_view_bottom.menu.removeItem(R.id.navigation_students)
            }
            else -> return
        }
    }

    private fun showBottomDialog() {
        val dialog = BottomSheetDialog(this)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)
        dialog.setContentView(bottomSheet)

        val bottomSheetMenu1 = bottomSheet.findViewById<LinearLayout>(R.id.bottom_sheet_menu_1)
        bottomSheetMenu1.setOnClickListener {

            val intent = Intent(this@MainActivity, NewMemberActivity::class.java)
            startActivity(intent)
            dialog.dismiss()

        }

        val bottomSheetMenu2 = bottomSheet.findViewById<LinearLayout>(R.id.bottom_sheet_menu_2)
        bottomSheetMenu2.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showSettingsActivity() {

        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

    private fun showKioskActivity() {

        val kioskActivity = Intent(this, FaceRecognitionActivity::class.java)
        startActivity(kioskActivity)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.removeSelectedGrade()
    }

}
