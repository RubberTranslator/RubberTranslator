#!/bin/bash
clear
RED='\033[0;31m'
GRN='\033[0;32m'
GOLDEN='\033[0;33m'
BLU='\033[0;34m'
NC='\033[0m'
echo ""
echo ""
echo -e "提示RubberTranslator已损坏，无法打开修复工具 ${RED}"
echo ""
echo -e "${GOLDEN}请先尝试直接打开软件，如果不行再来尝试修复${NC}"
echo -e "${BLU}请输入电脑解锁密码（输入过程中密码是看不见的），输入完成后按下回车键${NC}"
sudo spctl --master-disable
echo -e "执行结果：${GRN}修复成功！${NC}您现在可以正常运行 RubberTranslator 了。"
echo ""
echo ""
echo -e "本窗口可以关闭啦！"
