package com.dt.learning.myapp2.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.dt.learning.myapp2.R
import com.dt.learning.myapp2.databinding.FragmentTasksBinding
import com.dt.learning.myapp2.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel : TasksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)
        val taskAdapter = TasksAdapter()

        binding.apply {

            recyclerViewTasks.apply {

                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)

            }

        }

        viewModel.tasks.observe(viewLifecycleOwner) { listTasks ->
            taskAdapter.submitList(listTasks)
        }

        setupMenu()

    }


    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {

            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_task, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView

                searchView.onQueryTextChanged {query ->
                    viewModel.searchQuery.value = query
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sort_by_name -> {
                        // TODO
                        true
                    }
                    R.id.action_sort_by_date_created -> {
                        //TODO
                        true
                    }
                    R.id.action_hide_completed_tasks -> {
                        menuItem.isChecked = !menuItem.isChecked
                        //TODO
                        true
                    }
                    R.id.action_delete_all_completedd_tasks -> {
                        // TODO
                        true
                    }
                    else -> false
                }

            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


}