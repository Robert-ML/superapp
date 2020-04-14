package nl.tudelft.trustchain.currencyii.ui.bitcoin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_dao_login_choice.*
import nl.tudelft.trustchain.currencyii.R
import nl.tudelft.trustchain.currencyii.coin.BitcoinNetworkOptions
import nl.tudelft.trustchain.currencyii.coin.WalletManagerAndroid
import nl.tudelft.trustchain.currencyii.coin.WalletManagerConfiguration

/**
 * A simple [Fragment] subclass.
 * Use the [DaoLoginChoice.newInstance] factory method to
 * create an instance of this fragment.
 */
class DaoLoginChoice : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        load_existing_button.setOnClickListener {
            if (!WalletManagerAndroid.isInitialized()) {
                val config = WalletManagerConfiguration(BitcoinNetworkOptions.PRODUCTION)
                WalletManagerAndroid.Factory(this.requireContext().applicationContext)
                    .setConfiguration(config).init()

                findNavController().navigate(
                    DaoLoginChoiceDirections.actionDaoLoginChoiceToBitcoinFragment(
                        true
                    )
                )
            } else {
                findNavController().navigate(
                    DaoLoginChoiceDirections.actionDaoLoginChoiceToBitcoinFragment()
                )
            }
        }

        import_create_button.setOnClickListener {
            findNavController().navigate(DaoLoginChoiceDirections.actionDaoLoginChoiceToDaoImportOrCreate())
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dao_login_choice, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaoLoginChoice()
    }
}
