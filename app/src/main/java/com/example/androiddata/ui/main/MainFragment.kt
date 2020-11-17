package com.example.androiddata.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.androiddata.R
import com.example.androiddata.TAG
import com.example.androiddata.VIEW_TYPE_GRID
import com.example.androiddata.VIEW_TYPE_LIST
import com.example.androiddata.data.Monster
import com.example.androiddata.ui.shared.SharedViewModel
import com.example.androiddata.utilities.PrefsHelper

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
    // with declaration here we can access it where ever we want in this class
    private lateinit var viewModel: SharedViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var navController: NavController
    private lateinit var adapter: MainRecyclerAdapter

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

        // setHasOptionsMenu(true) is not needed in Activities
        // but in Fragment OptionsMenu doesn't work without setHasOptionsMenu(true)
        // it makes fragment to listen OptionsMenu actions
        setHasOptionsMenu(true)

        // assignment of view object for assignment of recycler object
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        // get my layoutStyle from preferences. this code executed when fragment starts up
        val layoutStyle = PrefsHelper.getItemType(requireContext())
        recyclerView.layoutManager =
                if (layoutStyle == VIEW_TYPE_GRID) GridLayoutManager(requireContext(), 2)
                else LinearLayoutManager(requireContext())

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
            // adapter splited to property declaration
            adapter = MainRecyclerAdapter(requireContext(), it, this)
            // assign adapter to the recycler
            recyclerView.adapter = adapter
//            after receiving data hides refreshing icon on display
            swipeLayout.isRefreshing = false
        })

        viewModel.activityTitle.observe(viewLifecycleOwner, {
            // set Activity Title to value of LiveData object
            requireActivity().title = it
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

    // display options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // actions on selecting options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_view_grid -> {
                changeViewType(VIEW_TYPE_GRID)
            }
            R.id.action_view_list -> {
                changeViewType(VIEW_TYPE_LIST)
            }
            R.id.action_settings -> {
                navController.navigate(R.id.settingsActivity)
            }
        }
        return true
    }

    private fun changeViewType(viewType: String = VIEW_TYPE_GRID) {
        // set preferences item type
        PrefsHelper.setItemType(requireContext(), viewType)
        // override recyclerView's layoutManager
        recyclerView.layoutManager =
            if (viewType == VIEW_TYPE_GRID) GridLayoutManager(requireContext(), 2)
            else LinearLayoutManager(requireContext())
        // reassigning the adapter to recyclerView
        recyclerView.adapter = adapter
    }

    // one of standard lifeCycle functions for applying changes after settings changed
    override fun onResume() {
        super.onResume()
        // read signature from preference setting and update activityTitle MutableLiveData
        viewModel.updateActivityTitle()
    }

}
