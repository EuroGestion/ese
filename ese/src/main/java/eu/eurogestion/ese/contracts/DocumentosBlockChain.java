package eu.eurogestion.ese.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.16.
 */
@SuppressWarnings("rawtypes")
public class DocumentosBlockChain extends Contract {
    public static final String BINARY = "0x608060405234801561001057600080fd5b506104ea806100206000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c806391f772b21461003b578063efd69f51146100e2575b600080fd5b6100676004803603602081101561005157600080fd5b81019080803590602001909291905050506101a7565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100a757808201518184015260208101905061008c565b50505050905090810190601f1680156100d45780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6101a5600480360360408110156100f857600080fd5b81019080803590602001909291908035906020019064010000000081111561011f57600080fd5b82018360208201111561013157600080fd5b8035906020019184600183028401116401000000008311171561015357600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f82011690508083019250505050505050919291929050505061025b565b005b60606000808381526020019081526020016000208054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561024f5780601f106102245761010080835404028352916020019161024f565b820191906000526020600020905b81548152906001019060200180831161023257829003601f168201915b50505050509050919050565b600082116102d1576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260178152602001807f456c206964206e6f20707565646520736572206365726f00000000000000000081525060200191505060405180910390fd5b6000815111610348576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601c8152602001807f456c2068617368206e6f20707565646520657374617220766163696f0000000081525060200191505060405180910390fd5b600080600084815260200190815260200160002080546001816001161561010002031660029004905011156103e5576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260168152602001807f456c20646f63756d656e746f207961206578697374650000000000000000000081525060200191505060405180910390fd5b80600080848152602001908152602001600020908051906020019061040b929190610410565b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061045157805160ff191683800117855561047f565b8280016001018555821561047f579182015b8281111561047e578251825591602001919060010190610463565b5b50905061048c9190610490565b5090565b6104b291905b808211156104ae576000816000905550600101610496565b5090565b9056fea265627a7a72315820210261e95847c0e65aa2d5ceb3aabc290baa1b178dd199651f27566e0d37e4a964736f6c63430005100032";

    public static final String FUNC_ADDDOCUMENTO = "addDocumento";

    public static final String FUNC_GETHASHDOCUMENTO = "getHashDocumento";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("2020", "0x46A79626F5febC1675E043274f565C49e9A4D0fC");
    }

    @Deprecated
    protected DocumentosBlockChain(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DocumentosBlockChain(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DocumentosBlockChain(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DocumentosBlockChain(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> addDocumento(BigInteger _id, String _hash) {
        final Function function = new Function(
                FUNC_ADDDOCUMENTO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_id), 
                new org.web3j.abi.datatypes.Utf8String(_hash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> getHashDocumento(BigInteger idBuscado) {
        final Function function = new Function(FUNC_GETHASHDOCUMENTO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(idBuscado)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @Deprecated
    public static DocumentosBlockChain load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DocumentosBlockChain(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DocumentosBlockChain load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DocumentosBlockChain(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DocumentosBlockChain load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DocumentosBlockChain(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DocumentosBlockChain load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DocumentosBlockChain(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DocumentosBlockChain> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DocumentosBlockChain.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DocumentosBlockChain> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DocumentosBlockChain.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DocumentosBlockChain> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DocumentosBlockChain.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DocumentosBlockChain> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DocumentosBlockChain.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }
}
