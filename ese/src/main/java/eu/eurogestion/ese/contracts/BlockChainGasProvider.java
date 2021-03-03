package eu.eurogestion.ese.contracts;

import java.math.BigInteger;

import org.web3j.tx.gas.StaticGasProvider;

public class BlockChainGasProvider extends StaticGasProvider {
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(9_000_000);
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(0L);

    public BlockChainGasProvider() {
        super(GAS_PRICE, GAS_LIMIT);
    }
}
