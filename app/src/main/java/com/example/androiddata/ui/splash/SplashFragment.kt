package com.example.androiddata.ui.splash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.example.androiddata.R

// Starting in android 6 dangerous permissions have to be requested at runtime
// If you declare dangerous permission in Manifest it will be granted during installation
// for older android, but for recent versions you have to add code yourself
// that's what we will do in this splash fragment

// PERMISSION_REQUEST_CODE can be any value. it is used for specification of permission
const val PERMISSION_REQUEST_CODE = 1001

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (ContextCompat.checkSelfPermission(
                // context
                requireContext(),
                // Permission. Manifest from android.Manifest
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // if permission is granted continue to display the fragment
            // don't want to pause on this fragment
            displayMainFragment()
        } else {
            // else request the permissions. even if it is a single permission it have to be in array
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE)
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // if requested permission code matches and permission is granted displays MainFragment
        // else
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayMainFragment()
            }
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_LONG).show()
        }
    }

    private fun displayMainFragment() {
        // passed requireActivity() because NavController is located in Activity rather then Fragment
        val navController = Navigation.findNavController(
            requireActivity(), R.id.nav_host
        )
        // navigates to the destination of action in navigation element
        navController.navigate(R.id.action_nav_main, null,
            // Builds navigation options.
            // We don't want to come back to splashFragment when up button is pressed
            NavOptions.Builder()
                    // inclusive: true means when you come back to R.id.splashFragment navigation
                    // pop ups and skip this fragment by going upper and exit activity
                .setPopUpTo(R.id.splashFragment, true)
                .build()
        )
    }

}