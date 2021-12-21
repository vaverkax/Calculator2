package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.calculator.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var binding: ActivityMainBinding
    private val calculator = CommandProcessor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val btn = arrayOfNulls<Button>(10)
        for (i in 0..9) {
            val btnName = "btn$i"
            val btnId = resources.getIdentifier(btnName, "id", packageName)
            btn[i] = findViewById(btnId)
            btn[i]?.setOnClickListener {
                when (binding.txtInput.text.isNullOrEmpty()) {
                    true -> binding.txtInput.text = i.toString()
                    else -> {
                        when (binding.txtInput.text.toString().first() == '0') {
                            true -> {
                                when (binding.txtInput.text.count()) {
                                    1 -> {
                                        when (i) {
                                            0 -> { binding.txtInput.text = "0" }
                                            else -> { binding.txtInput.text = i.toString() }
                                        }
                                    }
                                    else -> { binding.txtInput.text = binding.txtInput.text.toString() + i }
                                }
                            }
                            else -> { binding.txtInput.text = binding.txtInput.text.toString() + i }
                        }
                    }
                }
                setNeededTextSize()
            }
        }

        binding.btnMultiply.setOnClickListener {
            binding.txtInput.text = binding.txtInput.text.toString() + "*"
            setNeededTextSize()
        }

        binding.btnDivide.setOnClickListener {
            binding.txtInput.text = binding.txtInput.text.toString() + "/"
            setNeededTextSize()
        }

        binding.btnDot.setOnClickListener {
            binding.txtInput.text = binding.txtInput.text.toString() + "."
            setNeededTextSize()
        }

        binding.btnSubtract.setOnClickListener {
            binding.txtInput.text = binding.txtInput.text.toString() + "-"
            setNeededTextSize()
        }

        binding.btnAdd.setOnClickListener {
            binding.txtInput.text = binding.txtInput.text.toString() + "+"
            setNeededTextSize()
        }

        binding.btnDel.setOnClickListener {
            binding.txtInput.text = binding.txtInput.text.toString().dropLast(1)
            setNeededTextSize()
        }

        binding.btnEqual.setOnClickListener {
            val (result, message) = calculator.startOperations(calculator,binding.txtInput.text.toString())
            when (result) {
                null -> {
                    Snackbar.make(view, message,Snackbar.LENGTH_SHORT)
                        .setAction("OK") { }
                        .show()
                    binding.txtInput.text = "0"
                }
                else -> {
                    binding.txtInput.text = result.toPlainString()
                }
            }
            setNeededTextSize()
        }
    }

    private fun setNeededTextSize() {
        when {
            binding.txtInput.text.length >= 10 -> {
                binding.txtInput.textSize = 30f
            }
            binding.txtInput.text.length >= 6 -> {
                binding.txtInput.textSize = 35f
            }
            else -> {
                binding.txtInput.textSize = 48f
            }
        }
    }
}
