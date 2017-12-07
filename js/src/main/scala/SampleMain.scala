import com.thoughtworks.binding.Binding.{Var, Vars, Constants}
import com.thoughtworks.binding.FutureBinding
import com.thoughtworks.binding.dom

import org.scalajs.dom.document
import org.scalajs.dom.raw.Event
import org.scalajs.dom.html.Input
import org.scalajs.dom.ext.Ajax

import scala.scalajs.js.JSON
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{ Failure, Success }

import scala.scalajs.js.annotation.JSExport

/**
  * @author 杨博 (Yang Bo) &lt;pop.atry@gmail.com&gt;
  */
@JSExport
object SampleMain {

  case class Contact(name: Var[String], email: Var[String])

  val data = Vars.empty[Contact]

  @dom
  def table = {
    <table border="1" cellPadding="5">
      <thead>
        <tr>
          <th>Name</th>
          <th>E-mail</th>
          <th>Operation</th>
        </tr>
      </thead>
      <tbody>
        {
          for (contact <- data) yield {
            <tr>
              <td>
                {contact.name.bind}
              </td>
              <td>
                {contact.email.each}
              </td>
              <td>
                <button
                  onclick={ event: Event =>
                    contact.name := "New Name"
                  }
                >
                  Modify the name
                </button>
              </td>
            </tr>
          }
        }
      </tbody>
    </table>
    <div>
      <button
        onclick={ event: Event =>
          data.value += Contact(Var("Thomas"), Var("Je t'aime!"))
        }
      >
        Add a contact
      </button>
    </div>
  }

  @dom
  def render = {
    val logs = Seq("Hello", "Binding.scala")
    <div>
      { Constants(logs:_*).map { item => <div>{ item }</div>}  }
    </div>
  }

  @dom
  def fender = {
    val githubUserName = Var("")
    def inputHandler = { event: Event => githubUserName := event.currentTarget.asInstanceOf[Input].value }
    <div>
      <input type="text" oninput={ inputHandler }/>
      <hr/>
      {
        val name = githubUserName.bind
        if (name == "") {                                                                     // 如果用户名为空：
          <div>Please input your Github user name</div>                                       // 显示提示文字；
        } else {                                                                              // 如果用户名非空：
          val githubResult = FutureBinding(Ajax.get(s"https://api.github.com/users/${name}")) // 发起 Github API 请求，
          githubResult.bind match {                                                           // 并根据 API 结果显示不同的内容：
            case None =>                                                                      // 如果尚未加载完毕：
              <div>Loading the avatar for { name }</div>                                      // 显示提示信息；
            case Some(Success(response)) =>                                                   // 如果成功加载：
              val json = JSON.parse(response.responseText)                                    // 把回应解析成 JSON；
              <img src={ json.avatar_url.toString }/>                                         // 并显示头像；
            case Some(Failure(exception)) =>                                                  // 如果加载时出错，
              <div>{ exception.toString }</div>                                               // 显示错误信息。
            }
          }
        }
    </div>
  }

  @JSExport
  def main(): Unit = {
    // dom.render(document.body, table)
    dom.render(document.body, fender)
  }

}
