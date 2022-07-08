package com.example.bankline.ui.statement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bankline.R
import com.example.bankline.data.remote.State
import com.example.bankline.databinding.ActivityBankStatementBinding
import com.example.bankline.databinding.ActivityWelcomeBinding
import com.example.bankline.domain.Correntista
import com.example.bankline.domain.Movimentacao
import com.example.bankline.domain.TipoMovimentacao
import com.google.android.material.snackbar.Snackbar

class BankStatementActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ACCOUNT_HOLDER = "com.example.bankline.ui.statement.EXTRA_ACCOUNT_HOLDER"
    }

    private val binding by lazy {
        ActivityBankStatementBinding.inflate(layoutInflater)
    }

    private val accountHolder by lazy {
        intent.getParcelableExtra<Correntista>(EXTRA_ACCOUNT_HOLDER) ?: throw IllegalArgumentException()
    }

    private val viewModel by viewModels<BankStatementViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvBankStatement.layoutManager = LinearLayoutManager(this)

        findBankStatement()

        binding.srlBankStatement.setOnRefreshListener { findBankStatement() }
    }

    private fun findBankStatement() {
       viewModel.findBankStatement(accountHolder.id).observe(this) { state ->
           when(state) {
               is State.Sucess -> {
                   binding.rvBankStatement.adapter = state.data?.let { BankStatementAdapter(it) }
                   binding.srlBankStatement.isRefreshing = false
               }
               is State.Error -> {
                   state.message?.let { Snackbar.make(binding.rvBankStatement, it, Snackbar.LENGTH_LONG).show() }
                   binding.srlBankStatement.isRefreshing = false
               }
               State.Wait -> {
                   binding.srlBankStatement.isRefreshing = true
               }
           }
       }
    }
}