package com.example.projectskelton.presentation.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.projectskelton.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {


}