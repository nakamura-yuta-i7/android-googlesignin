package com.dena.googlesignin

import kotlinx.android.synthetic.main.activity_main.*

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView

import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.drive.Drive
import me.mattak.moment.Moment

class MainActivity : AppCompatActivity(),
    GoogleApiClient.OnConnectionFailedListener,
    View.OnClickListener {

    companion object {
        private val TAG = "MainActivity"
        private val RC_SIGN_IN = 9001
    }

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mStatusTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sign_in_button.setOnClickListener(this)
        sign_out_button.setOnClickListener(this)
        disconnect_button.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Drive.SCOPE_FILE)
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .addApi(Drive.API)
            .build()

        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
    }

    public override fun onStart() {
        super.onStart()

        val opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
        if (opr.isDone) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in")
            val result = opr.get()
            handleSignInResult(result)
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback { googleSignInResult ->
                handleSignInResult(googleSignInResult)
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            val acct = result.signInAccount
            Log.d(TAG, acct!!.email)

            updateUI(true)
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false)
        }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
            updateUI(false)
        }
    }

    private fun revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback {
            updateUI(false)
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult)
    }


    private fun updateUI(signedIn: Boolean) {
        if (signedIn) {
            sign_in_button.visibility = View.GONE
            sign_out_button.visibility = View.VISIBLE
            disconnect_button.visibility = View.VISIBLE
        } else {
            sign_in_button.visibility = View.VISIBLE
            sign_out_button.visibility = View.GONE
            disconnect_button.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> signIn()
            R.id.sign_out_button -> signOut()
            R.id.disconnect_button -> revokeAccess()
        }
    }

    fun onFolderCreationButton(v: View) {
//        Log.d("onFolderCreationButton", "koko1")
//        val name = "New Folder"
//        val changeSet = MetadataChangeSet.Builder()
//                .setTitle(name).build()
//        Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(
//                mGoogleApiClient, changeSet).setResultCallback(ResultCallback {
//            result ->
//            if ( result.status.isSuccess ) {
//                Log.d("onFolderCreationButton", result.driveFolder.driveId.toString())
//            } else {
//                Log.d("onFolderCreationButton", "Error while trying to create the folder")
//            }
//        })
        var dateTimeString = Moment()
        MyGoogleDrive(mGoogleApiClient)
            .createRootFolder("New Folder ${dateTimeString}") {
                driveFolder ->
                driveFolder.driveFolder.getMetadata(mGoogleApiClient).setResultCallback { metadataResult ->
                    Log.d("getMetadata", metadataResult.metadata.toString())
                }
            }
    }

    fun tappedFolderInFolder(v: View) {
//        Drive.DriveApi.fetchDriveId(mGoogleApiClient, "0B0vk9XZ3993_Q3NBZ21uM2JvYU0")
//                .setResultCallback(ResultCallback { r ->
//                    val parentFolder = r.driveId.asDriveFolder()
//                    val changeSet = MetadataChangeSet.Builder()
//                            .setTitle("My New Child Folder").build()
//                    parentFolder.createFolder(mGoogleApiClient, changeSet)
//                            .setResultCallback(ResultCallback { r ->
//                                Log.d("tappedFolderInFolder", r.driveFolder.toString())
//                            })
//                })
        var dateTimeString = Moment()
        MyGoogleDrive(mGoogleApiClient)
            .createFolderInFolder("Child Folder ${dateTimeString}", "0B0vk9XZ3993_Q3NBZ21uM2JvYU0") {
                it.driveFolder.getMetadata(mGoogleApiClient).setResultCallback {
                    Log.d("createFolderInFolder -> getMetadata", it.metadata.toString())
                }
            }
    }

    fun tappedCanAuthorizedButton(v: View) {
        Log.d("tappedCanAuthorizedButton", MyGoogleDrive(mGoogleApiClient).canAuthorized().toString())
    }

    fun tappedFolderNotExists(v: View) {
//        Log.d("tappedFolderNotExists", "koko");
//        val folder = Drive.DriveApi.fetchDriveId(mGoogleApiClient, "AAAAAAAAAA")
//        folder.setResultCallback(ResultCallback { r ->
//
//            Log.d("tappedFolderNotExists", (r.driveId != null).toString());
//            Log.d("tappedFolderNotExists", r.status.toString() );
//        })
        MyGoogleDrive(mGoogleApiClient!!).folderExists(id = "AAAAAAAAAAAA") {
            exists ->
            Log.d("koko", exists.toString())
        }
    }

    fun tappedFolderExists(v: View) {
//        Log.d("tappedFolderExists", "koko");
//        val folder = Drive.DriveApi.fetchDriveId(mGoogleApiClient, "0B0vk9XZ3993_Q3NBZ21uM2JvYU0")
//        folder.setResultCallback(ResultCallback { r ->
//
//            Log.d("tappedFolderNotExists", r.toString());
//            Log.d("tappedFolderNotExists", r.status.toString());
//        })
        MyGoogleDrive(mGoogleApiClient!!).folderExists(id = "0B0vk9XZ3993_Q3NBZ21uM2JvYU0") {
            exists: Boolean ->
            Log.d("koko", exists.toString())
        }
    }

    fun onErrorCatchButton(v: View) {
        Drive.DriveApi.fetchDriveId(mGoogleApiClient, "!!!!!")
            .setResultCallback(ResultCallback {
                Log.d("it.driveId", it.driveId?.toString() ?: "エルビス演算子" )
                Log.d("!!!!!!!!", it.status.statusMessage )
            })
    }

    fun gotoGoogleCalendar(v: View) {
        val intent = Intent(this, GoogleCalendarActivity::class.java)
        startActivity(intent)
    }
}