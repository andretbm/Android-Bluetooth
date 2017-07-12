package com.example.afs.ihome;

import java.util.Date;

/**
 * Created by fguimaraes on 11/07/2017.
 */

public class ItemMessageQuee  {

    private static final long serialVersionUID = 1L;

    private Date dataEnvio;
    private PedidoAcao.MSG msgToSend;
    private String estadoEnvio;  //ACK;TIMEOUT
    private String resposta;
    private String accao;
//    public InputStream inStream = null;
//    public OutputStream outStream = null;

    public ItemMessageQuee()
    {

    }


    public void setResposta(String resposta){
        this.resposta=resposta;
    }

    public String getResposta(){
        return new String(this.resposta);
    }

    public void setAccao(String accao){
        this.accao=accao;
    }

    public String getAccao(){
        return new String(this.accao);
    }

//    public void setInStream(InputStream inStream)
//    {
//        this.inStream=inStream;
//    }
//
//    public InputStream getInStream(){
//        return this.inStream;
//    }
//
//    public void setOutputStream(OutputStream outStream){
//        this.outStream=outStream;
//    }
//
//    public OutputStream getOutputStream()
//    {
//        return this.outStream;
//    }

    public void setDataEnvio(Date data)
    {
        this.dataEnvio=data;
    }

    public Date getDataEnvio()
    {
       return this.dataEnvio;
    }

    public void setEstadoEnvio(String estadoEnvio)
    {
        this.estadoEnvio=estadoEnvio;
    }

    public String getEstadoEnvio(){
        return new String(this.estadoEnvio);
    }

    public void setMsgToSend(PedidoAcao.MSG msg){
        this.msgToSend=msg;
    }

    public PedidoAcao.MSG getMsgToSend(){
        return  this.msgToSend;
    }

}
