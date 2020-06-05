package cu.marilasoft.suitetecsa.ui.gallery

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import cu.marilasoft.suitetecsa.CaptivePortal
import cu.marilasoft.suitetecsa.R
import cu.marilasoft.suitetecsa.SUserPortal

class GalleryFragment : Fragment() {

    private lateinit var appBar: AppBarLayout
    private lateinit var tabs: TabLayout
    private lateinit var vPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_nauta, container, false)
        val parent = container?.parent as View
        val lo = parent.parent as View
        appBar = lo.findViewById(R.id.appbar)
        tabs = TabLayout(activity)
        tabs.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"))
        appBar.addView(tabs)
        vPager = root.findViewById(R.id.nauta_pager)

        val pagerAdapter = ViewPagerAdapter(fragmentManager!!)
        vPager.adapter = pagerAdapter

        tabs.setupWithViewPager(vPager)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appBar.removeView(tabs)
    }

    inner class ViewPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
        var pageTitles =
            arrayOf("WIFI", "Portal")

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return CaptivePortal()
                1 -> return SUserPortal()
            }
            return null
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return pageTitles[position]
        }
    }
}