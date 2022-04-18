package ar.net.rainbyte.resistorvaluecalculator

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import ar.net.rainbyte.resistorvaluecalculator.databinding.ActivityMainBinding

class MainActivity : Activity() {

    fun Spinner.selected(action: (position:Int) -> Unit) {
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                action(position)
            }
        }
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var resistorValueTemplate: String

    var figure1: Int = 0
    var figure2: Int = 0
    var figure3: Int = 0
    var multiplier: Float = 0f
    var multiplierTag: String = ""
    var tolerance: Float = 0f
    var temperature: Int = 0
    var bands: Int = 4

    private fun updateResistorValue() {
        val base = if (bands > 4) figure1 * 100 else 0 + figure2 * 10 + figure3
        val resistorValue = base * multiplier
        val resistorValueFormat = if (resistorValue % 1.0 != 0.0) { "%s" } else { "%.0f" }
        val resistorValueString = String.format(resistorValueFormat, resistorValue)
        binding.textviewResistorValue.text = (if (bands == 6) String.format(
            resistorValueTemplate,
            resistorValueString,
            multiplierTag,
            tolerance,
            temperature
        )
        else String.format(
            resistorValueTemplate,
            resistorValueString,
            multiplierTag,
            tolerance,
            temperature
        )).toString()
    }

    private fun updateBands() {
        if (bands < 6) {
            binding.spinnerTemperature.visibility = View.GONE
            if (bands < 5) binding.spinnerFigure1.visibility = View.GONE else binding.spinnerFigure1.visibility = View.VISIBLE
            resistorValueTemplate = resources.getString(R.string.resistor_value_template)
        } else {
            binding.spinnerTemperature.visibility = View.VISIBLE
            resistorValueTemplate = resources.getString(R.string.resistor_value_template_temperature)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val multiplierValues: Array<String> = resources.getStringArray(R.array.multiplier_values)
        val toleranceValues: Array<String> = resources.getStringArray(R.array.tolerance_values)
        val temperatureValues: Array<String> = resources.getStringArray(R.array.temperature_values)
        val bandValues: Array<String> = resources.getStringArray(R.array.band_values)
        resistorValueTemplate = resources.getString(R.string.resistor_value_template)
        binding.spinnerTemperature.visibility = View.GONE
        binding.spinnerFigure1.visibility = View.GONE

        val figure1Adapter = ArrayAdapter.createFromResource(this, R.array.figure_values, android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFigure1.adapter = figure1Adapter
        binding.spinnerFigure1.selected {
            figure1 = it
            updateResistorValue()
        }

        val figure2Adapter = ArrayAdapter.createFromResource(this, R.array.figure_values, android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFigure2.adapter = figure2Adapter
        binding.spinnerFigure2.selected {
            figure2 = it
            updateResistorValue()
        }

        val figure3Adapter = ArrayAdapter.createFromResource(this, R.array.figure_values, android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFigure3.adapter = figure3Adapter
        binding.spinnerFigure3.selected {
            figure3 = it
            updateResistorValue()
        }

        val multiplierAdapter = ArrayAdapter.createFromResource(this, R.array.multiplier_values, android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMultiplier.adapter = multiplierAdapter
        binding.spinnerMultiplier.selected {
            val (a, b) = when (multiplierValues[it]) {
                "Black"  ->    1f to ""
                "Brown"  ->   10f to ""
                "Red"    ->  100f to ""
                "Orange" ->    1f to "K"
                "Yellow" ->   10f to "K"
                "Green"  ->  0.1f to "M" // 100K
                "Blue"   ->    1f to "M"
                "Violet" ->   10f to "M"
                "Grey"   ->  100f to "M"
                "White"  ->    1f to "G"
                "Gold"   ->  0.1f to ""
                "Silver" -> 0.01f to ""
                else     ->    0f to ""
            }
            multiplier = a
            multiplierTag = b
            updateResistorValue()
        }

        val toleranceAdapter = ArrayAdapter.createFromResource(this, R.array.tolerance_values, android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTolerance.adapter = toleranceAdapter
        binding.spinnerTolerance.selected {
            tolerance = when (toleranceValues[it]) {
                "Brown"  -> 1f
                "Red"    -> 2f
                "Green"  -> 0.5f
                "Blue"   -> 0.25f
                "Violet" -> 0.1f
                "Grey"   -> 0.05f
                "Gold"   -> 5f
                "Silver" -> 10f
                "None"   -> 20f
                else     -> 0f
            }
            updateResistorValue()
        }

        val temperatureAdapter = ArrayAdapter.createFromResource(this, R.array.temperature_values, android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTemperature.adapter = temperatureAdapter
        binding.spinnerTemperature.selected {
            temperature = when (temperatureValues[it]) {
                "Brown"  -> 100
                "Red"    -> 50
                "Orange" -> 15
                "Yellow" -> 25
                else     -> 0
            }
            updateResistorValue()
        }

        val bandsAdapter = ArrayAdapter.createFromResource(this, R.array.band_values, android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBands.adapter = bandsAdapter
        binding.spinnerBands.selected {
            bands = when (bandValues[it]) {
                "4 bands" -> 4
                "5 bands" -> 5
                "6 bands" -> 6
                else -> 4
            }
            updateBands()
            updateResistorValue()
        }
    }
}
