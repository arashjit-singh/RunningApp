package com.example.projectskelton.presentation.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.projectskelton.R
import com.example.projectskelton.presentation.ui.viewModels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    private val mainViewModel: StatisticsViewModel by viewModels()

}