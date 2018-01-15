pragma solidity ^0.4.18;

import {SafeMath} from "./SafeMath.sol";
import {NeworkToken} from "./NeworkToken.sol";

contract NeworkTokenIssue {

    address public tokenContractAddress;
    uint256 public lastBlockNumber;
    uint256 public lastYearTotalSupply = 10 * 10 ** 26; //init issue

    function NeworkTokenIssue (address _tokenContractAddress) public{
        tokenContractAddress = _tokenContractAddress;
        lastBlockNumber = block.number;
    }

    // anyone can call this function
    function issue() public  {
        // 1 block per 15 sec, 
        require(SafeMath.sub(block.number, lastBlockNumber) > 2102400);
        NeworkToken tokenContract = NeworkToken(tokenContractAddress);
        //adjust total supply every year
        lastYearTotalSupply = tokenContract.totalSupply(); 
        uint256 amount = SafeMath.div(SafeMath.mul(lastYearTotalSupply, 3), 100);
        assert(amount > 0);
        tokenContract.issue(amount);
        lastBlockNumber = block.number;
    }
}
