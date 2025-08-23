package com.helpmepls.slidepuzzle.frm

import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.helpmepls.slidepuzzle.R
import com.helpmepls.slidepuzzle.adt.BoardSizeAdapter
import com.helpmepls.slidepuzzle.model.BoardTitledSize
import com.helpmepls.slidepuzzle.vm.BoardOptionsVm

class BoardSizeSpinnerFrm : Fragment() {
    private val viewModel: BoardOptionsVm? by lazy {
        activity?.let {
            ViewModelProviders.of(it)[BoardOptionsVm::class.java]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(
            /* resource = */ R.layout.board_size_spinner_fragment,
            /* root = */ container,
            /* attachToRoot = */ false
        )
        val sizeSpinner = view.findViewById<Spinner>(R.id.board_size_spinner)
        val arrayAdapter = BoardSizeAdapter(
            /* context = */ activity as Context,
            /* resource = */ R.layout.game_toolbar_spinner_item,
            /* objects = */ BoardOptionsVm.PREDEFINED_BOARD_SIZE
        )

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sizeSpinner.adapter = arrayAdapter

        sizeSpinner.setSelection(arrayAdapter.getPosition(viewModel?.boardSize?.value))
        sizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selectedItem = parent.getItemAtPosition(position)
                selectedItem.let {
                    if (viewModel?.boardSize?.value?.equals(selectedItem) == true)
                        return

                    viewModel?.boardSize?.value = selectedItem as BoardTitledSize
                }
            }
        }

        return view
    }
}
