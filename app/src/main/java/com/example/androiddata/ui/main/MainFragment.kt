package com.example.androiddata.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.androiddata.R
import com.example.androiddata.TAG
import com.example.androiddata.data.Monster
import com.example.androiddata.ui.shared.SharedViewModel

// Fragment is a User Interface and is only responsible for managing the presentation
class MainFragment : Fragment(),
    // implementation of interface from RecyclerAdapter for listening of Recycler Item click
    MainRecyclerAdapter.MonsterItemListener {

/*
    companion object {
        fun newInstance() = MainFragment()
    }
*/

    // instance of viewModel, recyclerView, swipeLayout, navController
    private lateinit var viewModel: SharedViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        Initialization of Data Class
//        val monster = Monster("Bob", "myFile", "a caption", "a description", .19, 3)
//        Log.i(TAG, monster.toString())

        // in order to get reference to ActionBar Component
        // we have to explicitly cast Activity as AppCompatActivity
        (requireActivity() as AppCompatActivity).run {
            // removes up button. it is member of ActionBar Component
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        // assignment of view object for assignment of recycler object
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        // passed requireActivity() because NavController is located in Activity rather then Fragment
        navController = Navigation.findNavController(
            requireActivity(), R.id.nav_host
        )

        swipeLayout = view.findViewById(R.id.swipeLayout)
        // swipe event listener as a lambda expression that reacts on gesture
        swipeLayout.setOnRefreshListener {
            viewModel.refreshData()
        }

        // view model initialization with requireActivity() view model belongs to the Activity
        // with this approach we can pass data between fragments
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)
        // observation of LiveData
        viewModel.monsterData.observe(viewLifecycleOwner, {
            // StringBuilder() builds string objects

/*
            val monsterNames = StringBuilder()
            for (monster in it) {
                monsterNames.append(monster.monsterName).append("\n")
            }
            message.text = monsterNames
*/
            // instance of adapter, passing context, observed viewModel.monsterData
            // and Fragment itself as a listener
            val adapter = MainRecyclerAdapter(requireContext(), it, this)
            // assign adapter to the recycler
            recyclerView.adapter = adapter
//            after receiving data hides refreshing icon on display
            swipeLayout.isRefreshing = false
        })

        return view
    }

    override fun onMonsterItemClick(monster: Monster) {
        Log.i(TAG, "Selected monster: ${monster.monsterName}")
        // pass selected monster to the LiveData for observing it in DetailFragment
        viewModel.selectedMonster.value = monster
        // navigates to the destination of action in navigation element
        navController.navigate(R.id.action_nav_detail)
    }

/*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)
    }
*/

}
