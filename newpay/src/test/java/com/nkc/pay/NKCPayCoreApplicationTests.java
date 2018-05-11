package com.nkc.pay;

import com.nkc.pay.service.WithdrawService;
import com.nkc.pay.util.JSONUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NKCPayCoreApplicationTests {

    @Autowired
    private Web3j web3j;
    
		//Method For Get Balance of ETH
    @Test
    public void getBalbanceETH() throws Exception {
        String address = "wallet-address";
        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
				if (ethGetBalance != null) {
            System.out.println(Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER));

        }
    }
    
    //Method For Transfer ETH
     @Test
    public void transferETH() throws Exception {
    	  Credentials credentials = WalletUtils.loadCredentials("password", "wallet-file");
        TransactionReceipt transferReceipt = Transfer.sendFunds(
                web3j, credentials,
                "send-to-address",  // you can put any address here
                BigDecimal.ONE, Convert.Unit.ETHER)  // 1 wei = 10^-18 Ether
                .send();
    }

    //Method For Transfer ETH-Smart-Coin
    @Test
    public void transferETHSmartCoin() throws Exception {
        Credentials credentials = WalletUtils.loadCredentials("password", "wallet-file");
        String contractAddress = "smart-address";
        String fromAddress = credentials.getAddress();
        String toAddress = "send-to-address";
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        Address address = new Address(toAddress);
        Uint256 value = new Uint256(new BigInteger("money-amount(unit WEI)"));

        List<Type> parametersList = new ArrayList<>();
        parametersList.add(address);
        parametersList.add(value);

        List<TypeReference<?>> outList = new ArrayList<>();

        Function function = new Function(
                "transfer",  // function we're calling
                parametersList,  // Parameters to pass as Solidity Types
                outList);

        String encodedFunction = FunctionEncoder.encode(function);

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                new BigInteger("gas price"),
                new BigInteger("gas limit"),
                contractAddress,
                encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();
        System.out.println(transactionHash);
        file.delete();
    }

    //Method For Create A Wallet of The ETH or ETH-Smart-Coin
    @Test
    public void createWallet() throws Exception {
        String password = "password";
        File file = new File("create wallet-file dir");
        String fileName = WalletUtils.generateNewWalletFile("password",file, true);
    }
}