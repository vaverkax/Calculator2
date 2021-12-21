package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.calculator.databinding.ActivityMainBinding
import com.example.calculator.databinding.FragmentTextBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var binding: ActivityMainBinding
    private val calculator = CommandProcessor()
    private val textFragment = TextFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragmentContainer, textFragment)
            .addToBackStack(null)
            .commit()

        val btn = arrayOfNulls<Button>(10)
        for (i in 0..9) {
            val btnName = "btn$i"
            val btnId = resources.getIdentifier(btnName, "id", packageName)
            btn[i] = findViewById(btnId)
            btn[i]?.setOnClickListener {
                when (textFragment.getText().isNullOrEmpty()) {
                    true -> textFragment.setText(i.toString())
                    else -> {
                        when (textFragment.getText().first() == '0') {
                            true -> {
                                when (textFragment.getText().count()) {
                                    1 -> {
                                        when (i) {
                                            0 -> { textFragment.setText("0") }
                                            else -> { textFragment.setText(i.toString()) }
                                        }
                                    }
                                    else -> { textFragment.setText(textFragment.getText() + i) }
                                }
                            }
                            else -> { textFragment.setText(textFragment.getText() + i) }
                        }
                    }
                }
                textFragment.setNeededTextSize()
            }
        }

        binding.btnMultiply.setOnClickListener {
            textFragment.setText(textFragment.getText() + "*")
            textFragment.setNeededTextSize()
        }

        binding.btnDivide.setOnClickListener {
            textFragment.setText(textFragment.getText() + "/")
            textFragment.setNeededTextSize()
        }

        binding.btnDot.setOnClickListener {
            textFragment.setText(textFragment.getText() + ".")
            textFragment.setNeededTextSize()
        }

        binding.btnSubtract.setOnClickListener {
            textFragment.setText(textFragment.getText() + "-")
            textFragment.setNeededTextSize()
        }

        binding.btnAdd.setOnClickListener {
            textFragment.setText(textFragment.getText() + "+")
            textFragment.setNeededTextSize()
        }

        binding.btnDel.setOnClickListener {
            textFragment.setText(textFragment.getText().dropLast(1))
            textFragment.setNeededTextSize()
        }

        binding.btnEqual.setOnClickListener {
            val (result, message) = calculator.startOperations(calculator,textFragment.getText())
            when (result) {
                null -> {
                    Snackbar.make(view, message,Snackbar.LENGTH_SHORT)
                        .setAction("OK") { }
                        .show()
                    textFragment.setText("0")
                }
                else -> {
                    textFragment.setText(result.toPlainString())
                }
            }
            textFragment.setNeededTextSize()
        }
    }
}
