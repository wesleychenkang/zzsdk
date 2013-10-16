@echo off

@rem 资源路径
set s=x:\resource\android充值切图及说明文档\android充值切图\

@rem 目标路径
set p=y:\workspace\android\zzsdk\assets\zz_res\drawable\

@rem 前缀
set pr=cc_

@rem 类型
set t=
call :mycp 充值中心（购买道具）\recharge_ban.png recharge_ban.png
call :mycp 充值中心（社区进入）\充值_09.png charge_pull.png

set t=
call :myRm 兑换列表\ex_button.png ex_button.png
call :mycp 兑换列表\ex_button.9.png ex_button.9.png
call :myRm 兑换列表\ex_button_click.png ex_button_click.png
call :mycp 兑换列表\ex_button_click.9.png ex_button_click.9.png
call :mycp 兑换列表\ex_Right.png ex_right.png
call :mycp 兑换列表\ex_Right_click.png ex_right_click.png

set t=
@rem call :mycp 兑换详情\Background.png background.png
call :mycp 兑换详情\Background.9.png background.9.png
call :myRm 兑换详情\buy_button.png buy_button.png
call :myRm 兑换详情\buy_button_click.png buy_button_click.png
call :mycp 兑换详情\buy_button.9.png buy_button.9.png
call :mycp 兑换详情\buy_button_click.9.png buy_button_click.9.png

set t=
@rem call :mycp 公共资源\button.png button.png
call :mycp 公共资源\button.9.png button.9.png
@rem call :mycp 公共资源\button_click.png button_click.png
call :mycp 公共资源\button_click.9.png button_click.9.png
call :mycp 公共资源\help.png help.png
call :mycp 公共资源\money.png money.png
@rem call :mycp 公共资源\payment_input.png payment_input.png
call :mycp 公共资源\payment_input.9.png payment_input.9.png
@rem call :mycp 公共资源\recharge_input.png recharge_input.png
call :mycp 公共资源\recharge_input.9.png recharge_input.9.png
call :mycp 公共资源\tup_cft.png tup_cft.png
call :mycp 公共资源\tup_dx.png tup_dx.png
call :mycp 公共资源\tup_lt.png tup_lt.png
call :mycp 公共资源\tup_sjdx.png tup_sjdx.png
call :mycp 公共资源\tup_yd.png tup_yd.png
call :mycp 公共资源\tup_yl.png tup_yl.png
call :mycp 公共资源\tup_zfb.png tup_zfb.png
call :mycp 公共资源\tup_zyb.png tup_zyb.png
call :mycp 公共资源\zf_wxz.9.png zf_wxz.9.png
call :mycp 公共资源\zf_xz.9.png zf_xz.9.png

@pause
goto :eof

:myCp
set ss=%s%%1
set st=%p%%pr%%t%%2
if "%st:~-6%"==".9.png" (
    echo 需要预编译！ %ss%
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
