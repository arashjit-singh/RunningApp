package com.example.projectskelton.presentation.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.projectskelton.R
import com.example.projectskelton.databinding.FragmentSettingsBinding
import com.example.projectskelton.domain.util.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding

    @set:Inject
    var name = ""

    @set:Inject
    var weight = 80f

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etName.setText(name)
        binding.etWeight.setText(weight.toString())

        binding.btnApplyChanges.setOnClickListener {
            if (writeDataToSharedPref())
                Snackbar.make(requireView(), "Data Updated Successfully", Snackbar.LENGTH_SHORT)
                    .show()
            else
                Snackbar.make(requireView(), "Please enter all fields", Snackbar.LENGTH_SHORT)
                    .show()
        }

    }

    fun writeDataToSharedPref(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty())
            return false
        sharedPreferences.edit()
            .putString(Constants.KEY_NAME, name)
            .putFloat(Constants.KEY_WEIGHT, weight.toFloat())
            .apply()

        val toolbarText = "Let's Go, $name"
        requireActivity().findViewById<com.google.android.material.textview.MaterialTextView>(R.id.tvToolbarTitle).text =
            toolbarText
        return true
    }
}