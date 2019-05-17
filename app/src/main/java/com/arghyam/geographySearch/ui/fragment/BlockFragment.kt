package com.arghyam.geographySearch.ui.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.arghyam.R
import com.arghyam.geographySearch.adapter.BlockAdapter
import com.arghyam.geographySearch.model.BlockModel
import kotlinx.android.synthetic.main.content_state.*


class BlockFragment : Fragment() {
    private var blockList = ArrayList<BlockModel>()


    companion object {
        fun newInstance(): BlockFragment {
            var fragmentBlock = BlockFragment()
            var args = Bundle()
            fragmentBlock.arguments = args
            return fragmentBlock
        }

    }

    override fun onCreate(savedInstanceBlock: Bundle?) {
        super.onCreate(savedInstanceBlock)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceBlock: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_state, container, false)
    }

    override fun onActivityCreated(savedInstanceBlock: Bundle?) {
        super.onActivityCreated(savedInstanceBlock)

        init()
    }

    private fun init() {
        activity?.setTitle("Select Block")
        initRecyclerView()
    }

    private fun initRecyclerView() {
        stateRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = activity?.let { BlockAdapter(blockList, it) }
        stateRecyclerView.adapter = adapter

        blockList.add(BlockModel("Block 1"))
        blockList.add(BlockModel("Block 2"))

        blockList.add(BlockModel("Block 3"))

        blockList.add(BlockModel("Block 4"))

        blockList.add(BlockModel("Block 5"))

        blockList.add(BlockModel("Block 6"))

        blockList.add(BlockModel("Block 7"))

        blockList.add(BlockModel("Block 8"))




    }

}
