package com.tubes.medlab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tubes.medlab.news.Article
import com.tubes.medlab.news.RetrofitInstance
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val _newsList = MutableLiveData<List<Article>>()
    val newsList: LiveData<List<Article>> = _newsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        fetchTopHeadlines()
    }

    private fun fetchTopHeadlines() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = RetrofitInstance.api.getTopHeadlines("id", "health", "a057bf38fa69417da20bdb91394d24d6")
            if (response.isSuccessful) {
                _newsList.value = response.body()?.articles ?: emptyList()
                _isLoading.value = false
            } else {
                _errorMessage.value = "Error: ${response.message()}"
                _isLoading.value = false
            }
        }
    }
}
