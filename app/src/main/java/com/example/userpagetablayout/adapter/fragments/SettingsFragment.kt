package com.example.userpagetablayout.adapter.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.userpagetablayout.R
import com.example.userpagetablayout.view.SplashActivity
import com.example.userpagetablayout.databinding.FragmentSettingsBinding
import com.example.userpagetablayout.view.UserPageActivity
import com.example.userpagetablayout.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*


class SettingsFragment : Fragment() {

    companion object {
        var currentUser: User? = null
        var selectedPhotoUri: Uri? = null
        var binding: FragmentSettingsBinding? = null
        var newImage: Boolean = false
        var newImageUrl: String? = null
        val TAG = "Settings"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentSettingsBinding>(
            inflater,
            R.layout.fragment_settings,
            container,
            false
        )

        setUserData()
        setHasOptionsMenu(true)
        binding?.buttonSaveUser?.setOnClickListener {
            performSave()
        }
        binding?.selectedPhotoImageview?.setOnClickListener {
            newpic()
        }
        binding?.buttonAddImageSettings?.setOnClickListener {
            newpic()
        }

        // Inflate the layout for this fragment
        return binding?.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        activity?.menuInflater?.inflate(R.menu.go_back_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.goHome_settings -> {
                val navController = activity?.findNavController(R.id.myNavHostFragment)
                navController?.navigate(R.id.action_settings_to_homeFragment)
            }
            R.id.sign_out -> {
                val intent = Intent(activity, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Create Uri and bitmap from cropping selected image
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                val resultUri = result.uri
                selectedPhotoUri = resultUri
                val bitmap =
                    MediaStore.Images.Media.getBitmap(
                        activity?.contentResolver,
                        selectedPhotoUri
                    )
                binding?.selectedPhotoImageview?.setImageBitmap(bitmap)

                uploadImageToFireBase()

            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

    }

    private fun newpic() {
        //Initiate crop activity
        CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).setFixAspectRatio(true)
            .setAspectRatio(150, 150).start(requireContext(), this)
    }

    private fun uploadImageToFireBase() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/albumImages/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    newImage = true
                    newImageUrl = it.toString()
                }
            }

    }

    private fun performSave() {
        val EditTextEmail = binding?.EdittextEmailSettings?.text.toString()
        val EditTextPassword = binding?.passwordSettings?.text.toString()
        val EditTextUsername = binding?.EdittextUsernameSettings?.text.toString()

        val refCurrentUser = FirebaseAuth.getInstance().currentUser
        val currentPassword = binding?.CurrentpasswordSettings?.text.toString()
        val currentEmail = binding?.CurrentemailSettings?.text.toString()


        if (EditTextEmail.isNotEmpty() && EditTextPassword.isNotEmpty()) {

            val uid = FirebaseAuth.getInstance().uid

            if (EditTextUsername.isNotEmpty()) {
                //Update Username
                val refusername =
                    FirebaseDatabase.getInstance().getReference("/users/$uid/username")
                refusername.setValue(EditTextUsername)
            }

            if (newImage == true) {
                //Update Image
                val refimage =
                    FirebaseDatabase.getInstance().getReference("/users/$uid/profileImageUrl")
                refimage.setValue(newImageUrl)
            }


            val credential =
                EmailAuthProvider.getCredential(currentEmail.trim(), currentPassword.trim())
            Log.d(TAG, "the email is: ${currentEmail}, the password is ${currentPassword}")

            refCurrentUser?.reauthenticate(credential)?.addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                refCurrentUser.updateEmail(EditTextEmail.trim()).addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(activity, "Email was updated", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Log.d(TAG, "error on email update ${it.message}")
                    Toast.makeText(
                        activity,
                        "error on email update ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                refCurrentUser.updatePassword(EditTextPassword.trim()).addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d(TAG, "new passwors is $EditTextPassword")
                    Toast.makeText(activity, "Password was updated", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Log.d(TAG, "error on password update ${it.message}")
                    Toast.makeText(
                        activity,
                        "error on password update ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                //Sign in again
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(EditTextEmail, EditTextPassword)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        Log.d(TAG, "succesfully signed in $EditTextEmail $EditTextPassword")
                        val intent = Intent(activity, UserPageActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        Toast.makeText(activity, "Changes were saved", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "error on sign in ${it.message}")
                        Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
                    }


                //Toast.makeText(activity, "Succesfully updated the email",Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener {
                // Toast.makeText(context,"${it.message}",Toast.LENGTH_SHORT).show()
                Log.d("Settings", "error on reauthenticate ${it.message}")
                Toast.makeText(
                    activity,
                    "Please enter correct email and password",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else if (EditTextEmail.isNotEmpty() && EditTextPassword.isEmpty()) {
            val uid = FirebaseAuth.getInstance().uid

            if (EditTextUsername.isNotEmpty()) {
                //Update Username
                val refusername =
                    FirebaseDatabase.getInstance().getReference("/users/$uid/username")
                refusername.setValue(EditTextUsername)
            }

            if (newImage == true) {
                //Update Image
                val refimage =
                    FirebaseDatabase.getInstance().getReference("/users/$uid/profileImageUrl")
                refimage.setValue(newImageUrl)
            }


            val credential =
                EmailAuthProvider.getCredential(currentEmail.trim(), currentPassword.trim())
            Log.d(TAG, "the email is: ${currentEmail}, the password is ${currentPassword}")

            refCurrentUser?.reauthenticate(credential)?.addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                refCurrentUser.updateEmail(EditTextEmail.trim()).addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(activity, "Email was updated", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Log.d(TAG, "error on email update ${it.message}")
                    Toast.makeText(
                        activity,
                        "error on email update ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                Log.d(TAG, "the password is: $EditTextPassword, the email is $EditTextEmail")
                //sign in again
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(EditTextEmail, currentPassword)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        Log.d(TAG, "succesfully signed in")
                        val intent = Intent(activity, UserPageActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        Toast.makeText(activity, "Changes were saved", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "error on sign in ${it.message}")
                        Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
                    }


                //Toast.makeText(activity, "Succesfully updated the email",Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener {
                // Toast.makeText(context,"${it.message}",Toast.LENGTH_SHORT).show()
                Toast.makeText(
                    activity,
                    "Please enter correct email and password",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("Settings", "error on reauthenticate ${it.message}")
            }

        } else if (EditTextEmail.isEmpty() && EditTextPassword.isNotEmpty()) {
            val uid = FirebaseAuth.getInstance().uid

            if (EditTextUsername.isNotEmpty()) {
                //Update Username
                val refusername =
                    FirebaseDatabase.getInstance().getReference("/users/$uid/username")
                refusername.setValue(EditTextUsername)
            }

            if (newImage == true) {
                //Update Image
                val refimage =
                    FirebaseDatabase.getInstance().getReference("/users/$uid/profileImageUrl")
                refimage.setValue(newImageUrl)
            }


            val credential =
                EmailAuthProvider.getCredential(currentEmail.trim(), currentPassword.trim())
            Log.d(TAG, "the email is: ${currentEmail}, the password is ${currentPassword}")

            refCurrentUser?.reauthenticate(credential)?.addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }

                refCurrentUser.updatePassword(EditTextPassword.trim()).addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(activity, "Password was updated", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "new passwors is $EditTextPassword")
                }.addOnFailureListener {
                    Toast.makeText(
                        activity,
                        "error on password update ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(TAG, "error on password update ${it.message}")
                }

                //sign in again


                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(currentEmail, EditTextPassword)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        Log.d(TAG, "succesfully signed in $currentEmail")
                        val intent = Intent(activity, UserPageActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        Toast.makeText(activity, "Changes were saved", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "error on sign in ${it.message}")
                        Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
                    }


                //Toast.makeText(activity, "Succesfully updated the email",Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener {
                // Toast.makeText(context,"${it.message}",Toast.LENGTH_SHORT).show()
                Toast.makeText(
                    activity,
                    "Please enter correct email and password",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("Settings", "error on reauthenticate ${it.message}")
            }

        } else if (EditTextEmail.isEmpty() && EditTextPassword.isEmpty()) {
            val uid = FirebaseAuth.getInstance().uid

            if (EditTextUsername.isNotEmpty()) {
                //Update Username
                val refusername =
                    FirebaseDatabase.getInstance().getReference("/users/$uid/username")
                refusername.setValue(EditTextUsername)
            }

            if (newImage == true) {
                //Update Image
                val refimage =
                    FirebaseDatabase.getInstance().getReference("/users/$uid/profileImageUrl")
                refimage.setValue(newImageUrl)


            }
            val intent = Intent(activity, UserPageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            Toast.makeText(activity, "Changes were saved", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }


    }


    private fun setUserData() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")



        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)


                binding?.EdittextUsernameSettings?.hint = "${currentUser?.username}"
                binding?.EdittextEmailSettings?.hint =
                    "${FirebaseAuth.getInstance().currentUser?.email}"
                binding?.selectedPhotoImageview?.let {
                    Glide.with(this@SettingsFragment).load(currentUser?.profileImageUrl).into(it)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}


