package cu.marilasoft.suitetecsa


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass.
 */
class FDefault : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fdefault, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isSavedPortalUser()) {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
        } else {
            findNavController().navigate(R.id.to_singIn)
        }
    }

    private fun isSavedPortalUser(): Boolean {
        val portalUser = SharedApp.prefs.portalUser

        return portalUser != ""
    }


}
