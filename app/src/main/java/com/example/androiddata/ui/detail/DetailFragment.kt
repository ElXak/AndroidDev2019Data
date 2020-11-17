package com.example.androiddata.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.androiddata.R
import com.example.androiddata.databinding.FragmentDetailBinding
import com.example.androiddata.ui.shared.SharedViewModel

class DetailFragment : Fragment() {

    // instance of viewModel, navController
    private lateinit var viewModel: SharedViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // in order to get reference to ActionBar Component
        // we have to explicitly cast Activity as AppCompatActivity
        (requireActivity() as AppCompatActivity).run {
            // displays up button. it is member of ActionBar Component
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        // setHasOptionsMenu(true) is not needed in Activities
        // but in Fragment OptionsMenu doesn't work without setHasOptionsMenu(true)
        // it makes fragment to listen OptionsMenu actions
        setHasOptionsMenu(true)

        // passed requireActivity() because NavController is located in Activity rather then Fragment
        navController = Navigation.findNavController(
            requireActivity(), R.id.nav_host
        )

        // now both Main Fragment and Detail Fragment are look at the same ViewModel object
        // this makes ViewModel persistent for the life time of application
        // you can store data in ViewModel and it will be accessible through entire application
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)

        // observing LiveData monster from SharedViewModel instead we will use data binding library
//        viewModel.selectedMonster.observe(viewLifecycleOwner, {
//            Log.i(TAG, "Selected monster: ${it.monsterName}")
//        })

        // instead of classic inflating layout we will bind it
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_detail, container, false)
        // in data binding architecture each layout generates a special class
        // the name of class matches to the name of layout without spaces and special characters
        // fragment_detail.xml => FragmentDetailBinding
        val binding = FragmentDetailBinding.inflate(inflater, container, false)
        // updates binding as needed
        binding.lifecycleOwner = this
        // pass the data to the binding
        binding.viewModel = viewModel

        // return referance to the root element of the layout
        return binding.root
    }

    // up button is part of OptionsMenu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         // android.R.id.home is an android reserved id
        if (item.itemId == android.R.id.home) {
            // navigates back to MainFragment
            navController.navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

}