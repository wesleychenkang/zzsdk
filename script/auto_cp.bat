@echo off

@rem 资源路径
set s=x:\resource\android充值切图及说明文档\android充值切图\

@rem 目标路径
set p=%~dp0\..\assets\zz_res\drawable\

@rem 前缀
set pr=cc_

@rem 类型
set t=
call :myRm 充值中心（购买道具）\recharge_ban.png recharge_ban.png
call :mycp 充值中心（购买道具）\recharge_ban_2.9.png recharge_ban.9.png
@rem call :mycp 充值中心（社区进入）\充值_09.png charge_pull.png

set t=
call :myRm 兑换列表\ex_button.png ex_button.png
@rem call :mycp 兑换列表\ex_button.9.png ex_button.9.png
call :mycp 兑换列表\ex_button_2.9.png ex_button.9.png
call :myRm 兑换列表\ex_button_click.png ex_button_click.png
@rem call :mycp 兑换列表\ex_button_click.9.png ex_button_click.9.png
call :mycp 兑换列表\ex_button_click_2.9.png ex_button_click.9.png
call :mycp 兑换列表\ex_Right.png ex_right.png
call :mycp 兑换列表\ex_Right_click.png ex_right_click.png

set t=
@rem call :mycp 兑换详情\Background.png background.png
@rem call :mycp 兑换详情\Background.9.png background.9.png
call :mycp 兑换详情\Background_2.9.png background.9.png
call :myRm 兑换详情\buy_button.png buy_button.png
call :myRm 兑换详情\buy_button_click.png buy_button_click.png
call :mycp 兑换详情\buy_button.9.png buy_button.9.png
call :mycp 兑换详情\buy_button_click.9.png buy_button_click.9.png

set t=
@rem call :mycp 公共资源\button.png button.png
@rem call :mycp 公共资源\button.9.png button.9.png
@rem call :mycp 公共资源\button_click.png button_click.png
@rem call :mycp 公共资源\button_click.9.png button_click.9.png
call :mycp 公共资源\help.png help.png
call :mycp 公共资源\money.png money.png
@rem call :mycp 公共资源\payment_input.png payment_input.png
@rem call :mycp 公共资源\payment_input.9.png payment_input.9.png
call :mycp 公共资源\payment_input_2.9.png payment_input.9.png
@rem call :mycp 公共资源\recharge_input.png recharge_input.png
@rem call :mycp 公共资源\recharge_input.9.png recharge_input.9.png
call :mycp 公共资源\recharge_input_2.9.png recharge_input.9.png
@rem call :mycp 公共资源\tup_cft.png tup_cft.png
@rem call :mycp 公共资源\tup_dx.png tup_dx.png
@rem call :mycp 公共资源\tup_lt.png tup_lt.png
@rem call :mycp 公共资源\tup_sjdx.png tup_sjdx.png
@rem call :mycp 公共资源\tup_yd.png tup_yd.png
@rem call :mycp 公共资源\tup_yl.png tup_yl.png
@rem call :mycp 公共资源\tup_zfb.png tup_zfb.png
@rem call :mycp 公共资源\tup_zyb.png tup_zyb.png
@rem call :mycp 公共资源\zf_wxz.9.png zf_wxz.9.png
call :mycp 公共资源\zf_wxz_2.9.png zf_wxz.9.png
@rem call :mycp 公共资源\zf_xz.9.png zf_xz.9.png
call :mycp 公共资源\zf_xz_2.9.png zf_xz.9.png

set s=x:\resource\1029优化版drawable\drawable\
set p=%~dp0\..\assets\zz_res\drawable\
set pr=


:tup
set s=x:\resource\充值方式小图标1029\支付图标_android\
set p=%~dp0\..\assets\zz_res\drawable\
set pr=cc_
set t=
@rem call :mycp tup_cft.png tup_cft.png
@rem call :mycp tup_dezf.png tup_dezf.png
@rem call :mycp tup_dx.png tup_dx.png
@rem call :mycp tup_lt.png tup_lt.png
@rem call :mycp tup_sjdx.png tup_sjdx.png
@rem call :mycp tup_yd.png tup_yd.png
@rem call :mycp tup_yl.png tup_yl.png
@rem call :mycp tup_zfb.png tup_zfb.png
call :mycp tup_zyb.png tup_zyb.png


:login
set s=x:\resource\SDK登陆android\
set p=%~dp0\..\assets\zz_res\drawable\
set pr=login_
@rem call :mycp butten_lv.9.png button_lv.9.png
@rem call :mycp butten_lan.9.png button_lan.9.png
@rem call :mycp butten_hui.9.png button_hui.9.png
@rem call :mycp butten_huang.9.png button_huang.9.png
@rem call :mycp butten_@lv.9.png button_lv_click.9.png
@rem call :mycp butten_@lan.9.png button_lan_click.9.png
@rem call :mycp butten_@hui.9.png button_hui_click.9.png
@rem call :mycp butten_@huang.9.png button_huang_click.9.png
call :mycp butten_kuai.png button_kuai.png
call :mycp butten_kuai_anxia.png button_kuai_anxia.png

set s=x:\resource\1029优化版drawable\drawable\
set p=%~dp0\..\assets\zz_res\drawable\
set pr=
call :mycp login_button_huang.9.png login_button_huang.9.png
call :mycp login_button_huang_click.9.png login_button_huang_click.9.png
call :mycp login_button_hui.9.png login_button_hui.9.png
call :mycp login_button_hui_click.9.png login_button_hui_click.9.png
call :mycp login_button_lan.9.png login_button_lan.9.png
call :mycp login_button_lan_click.9.png login_button_lan_click.9.png
call :mycp login_button_lv.9.png login_button_lv.9.png
call :mycp login_button_lv_click.9.png login_button_lv_click.9.png
@rem call :mycp login_edit_press.9.png login_edit_press.9.png
call :mycp login_edit_press_2.9.png login_edit_press.9.png

:new_recharge_20131114
set s=x:\resource\充值\
set p=%~dp0\..\assets\zz_res\drawable\
set pr=cc_
set t=
@rem call :mycp 财付通.png tup_cft.png 
@rem call :mycp 大额支付.png tup_dezf.png
@rem call :mycp 中国电信.png tup_dx.png
@rem call :mycp 联通.png tup_lt.png
@rem call :mycp 短信.png tup_sjdx.png
@rem call :mycp 中国移动.png tup_yd.png
@rem call :mycp 银联.png tup_yl.png
@rem call :mycp 支付宝 .png tup_zfb.png
call :mycp 币.png money.png
call :mycp 标签栏.9.png title_bg.9.png
call :mycp 返回.png bt_back.png
call :mycp 返回点击.png bt_back_click.png
call :mycp 选择.png charge_pull.png
call :mycp 选择点击.png charge_pull_click.png
call :mycp 输入框.9.png input_bg.9.png
call :mycp 底板-9patch.9.png panel.9.png
call :mycp 选中.9.png paylist_sel.9.png
call :mycp 弹窗金币选中.9.png cand_sel.9.png
call :mycp 矩形1.9.png paylist_nor.9.png
@rem call :mycp button\确认充值.9.png button.9.png
@rem call :mycp button\确认充值点击.9.png button_click.9.png
call :mycp 改\确认充值.9.png button.9.png
call :mycp 改\确认充值点击.9.png button_click.9.png

call :mycp 图标\财付通.png tup_cft.png 
call :mycp 图标\大额支付.png tup_dezf.png
call :mycp 图标\中国电信.png tup_dx.png
call :mycp 图标\联通.png tup_lt.png
call :mycp 图标\短信.png tup_sjdx.png
call :mycp 图标\中国移动.png tup_yd.png
call :mycp 图标\银联.png tup_yl.png
call :mycp 图标\支付宝.png tup_zfb.png

@pause
goto :eof

:myCp
set ss=%s%%1
set st=%p%%pr%%t%%2
if "%st:~-6%"==".9.png" (
   @rem echo 需要预编译！ %ss%
   copy %ss% %st%
) else (
   copy %ss% %st%
)
goto :eof

:myRm
set ss=%s%%1
set st=%p%%pr%%t%%2
echo 删除 %st%
del %st%
goto :eof
