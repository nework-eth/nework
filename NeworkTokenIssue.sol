pragma solidity ^0.4.18;

import {SafeMath} from "./SafeMath.sol";
import {NeworkToken} from "./NeworkToken.sol";

contract NeworkTokenIssue {

    address public tokenContractAddress;
    uint256 public lastBlockNumber;
    uint256 public lastYearTotalSupply = 10 * 10 ** 26; //init issue
    bool    public isFirstYear = true; //not inflate in 2018

    function NeworkTokenIssue (address _tokenContractAddress) public{
        tokenContractAddress = _tokenContractAddress;
        lastBlockNumber = block.number;
    }

    // anyone can call this function
    function issue() public  {
        //ensure first year can not inflate
        if(isFirstYear){
            // 2102400 blocks is about one year, suppose it takes 15 seconds to generate a new block
            require(SafeMath.sub(block.number, lastBlockNumber) > 2102400);
            isFirstYear = false;
        }

        require(SafeMath.sub(block.number, lastBlockNumber) > 2102400);
        NeworkToken tokenContract = NeworkToken(tokenContractAddress);
        //adjust total supply every year
        lastYearTotalSupply = tokenContract.totalSupply(); 
        uint256 amount = SafeMath.div(SafeMath.mul(lastYearTotalSupply, 3), 100);
        require(amount > 0);
        tokenContract.issue(amount);
        lastBlockNumber = block.number;
    }
}
