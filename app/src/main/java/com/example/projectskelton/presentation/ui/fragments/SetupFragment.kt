package com.example.projectskelton.presentation.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.projectskelton.R
import com.example.projectskelton.databinding.FragmentSetupBinding
import com.example.projectskelton.domain.util.Constants.KEY_IS_FIRST_LOGIN
import com.example.projectskelton.domain.util.Constants.KEY_NAME
import com.example.projectskelton.domain.util.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {

    lateinit var binding: FragmentSetupBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var isFirstAppOpen = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isFirstAppOpen) {
            /*val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()

            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )*/
            popBackStack(savedInstanceState)
        }

        binding.tvContinue.setOnClickListener {
            if (writeDataToSharedPref())
                popBackStack(savedInstanceState)
            else
                Snackbar.make(requireView(), "Please enter all fields", Snackbar.LENGTH_SHORT)
                    .show()
        }

    }

    fun popBackStack(savedInstanceState: Bundle?) {

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.setupFragment, true)
            .build()

        findNavController().navigate(
            R.id.action_setupFragment_to_runFragment,
            savedInstanceState,
            navOptions
        )
    }

    fun writeDataToSharedPref(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty())
            return false
        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_IS_FIRST_LOGIN, false)
            .apply()

        val toolbarText = "Let's Go, $name"
        requireActivity().findViewById<com.google.android.material.textview.MaterialTextView>(R.id.tvToolbarTitle).text =
            toolbarText
        return true
    }
}